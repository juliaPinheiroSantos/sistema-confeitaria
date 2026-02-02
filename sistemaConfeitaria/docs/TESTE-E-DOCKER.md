# Como fazer o teste de cadastro de usuário e configurar no Docker

Este guia explica: (1) o que o teste faz, (2) como rodar o teste na sua máquina usando o PostgreSQL no Docker, e (3) como rodar o teste **dentro** do Docker.

---

## 1. O que o teste faz

O teste **UserRegistrationTest.cadastrarUsuario()** valida todo o fluxo de cadastro de um usuário:

1. **Cria as tabelas** no banco (`CreateTables.createAllTables()` no `@BeforeEach`).
2. **Cria uma Area** (ex.: "Centro", taxa 10.0) e persiste com `RepositoryArea.createArea()`.
3. **Busca a Area** por nome para obter o `id` gerado pelo banco.
4. **Cria um Address** (CEP, rua, número, etc.) vinculado a essa Area e persiste.
5. **Busca o Address** (via `findAllAddress()` e pega o último) para obter o `id`.
6. **Cria uma Person** (nome, sobrenome, email) vinculada a esse Address e persiste.
7. **Busca a Person** por email para obter o `id`.
8. **Cria um User** (senha em `char[]`) vinculado a essa Person e persiste com `RepositoryUser.createUser()`.
9. **Valida:** chama `RepositoryUser.findByEmailUser(email)` e verifica que o usuário foi salvo (não nulo, email/nome/sobrenome corretos).

Ou seja: o teste garante que **Area → Address → Person → User** funciona na ordem certa e que o usuário fica gravado e recuperável por email.

---

## 2. Conexão com o banco (DBConnection)

O projeto usa a classe **DBConnection**, que lê variáveis de ambiente para conectar ao PostgreSQL:

| Variável     | Uso        | Valor padrão (se não definir) |
|-------------|------------|-------------------------------|
| `DB_HOST`   | Host do BD | `127.0.0.1`                   |
| `DB_PORT`   | Porta      | `5434`                       |
| `DB_NAME`   | Nome do BD | `confeitaria`                |
| `DB_USER`   | Usuário    | `admin`                      |
| `DB_PASSWORD` | Senha    | `12345`                      |

- **Na sua máquina:** o banco está no Docker e a porta **5434** do host é mapeada para a 5432 do container. Por isso o padrão `127.0.0.1:5434` funciona quando você roda o teste na IDE ou com `mvn test`.
- **Dentro do Docker:** o teste roda em um container na mesma rede do Postgres; aí usamos `DB_HOST=postgres` e `DB_PORT=5432` (porta interna do serviço).

---

## 3. Rodar o teste na sua máquina (com Postgres no Docker)

Passo a passo:

### 3.1 Subir só o PostgreSQL

No diretório do projeto (onde está o `docker-compose.yml`):

```bash
docker-compose up -d postgres
```

Isso sobe o container **confeitaria-postgres** com o banco `confeitaria` (usuário `admin`, senha `12345`) na porta **5434** do seu PC.

### 3.2 (Opcional) Garantir banco limpo

Se quiser começar do zero (apaga dados e tabelas):

```bash
docker-compose down -v
docker-compose up -d postgres
```

O `-v` remove o volume; na próxima subida o banco é recriado vazio.

### 3.3 Rodar o teste

Ainda no diretório do projeto (onde está o `pom.xml`):

```bash
mvn test
```

Para rodar só a classe de cadastro de usuário:

```bash
mvn test -Dtest=UserRegistrationTest
```

Para rodar só o método:

```bash
mvn test -Dtest=UserRegistrationTest#cadastrarUsuario
```

O teste usa os valores **padrão** do DBConnection (`127.0.0.1`, `5434`, `confeitaria`, `admin`, `12345`), que batem com o que o `docker-compose` expõe. Não é obrigatório definir variáveis de ambiente para esse cenário.

### 3.4 Rodar pela IDE

1. Abra a classe **UserRegistrationTest**.
2. Clique com o botão direito na classe ou no método **cadastrarUsuario**.
3. Escolha **Run 'UserRegistrationTest'** ou **Run 'cadastrarUsuario()'**.

A IDE usa o mesmo DBConnection; desde que o Postgres esteja no ar em `127.0.0.1:5434`, o teste conecta no banco do Docker.

---

## 4. Rodar o teste dentro do Docker

Aqui o **próprio teste** roda em um container, usando a rede do `docker-compose` para falar com o Postgres.

### 4.1 O que muda

- O teste não roda na sua máquina, e sim num container com Maven.
- A conexão usa **host do serviço**: `DB_HOST=postgres` e `DB_PORT=5432` (porta interna do serviço `postgres`).

### 4.2 Comando

Com o serviço **test** configurado no `docker-compose` (veja a seção 5), na pasta do projeto:

```bash
docker-compose up -d postgres
docker-compose run --rm test
```

Ou, se o serviço `test` tiver `depends_on: postgres`:

```bash
docker-compose run --rm test
```

O `--rm` remove o container após o fim; a saída do `mvn test` aparece no terminal.

### 4.3 Por que fazer isso

- **CI/CD:** no pipeline (GitHub Actions, GitLab CI, etc.) você sobe o Postgres e roda `docker-compose run test` (ou equivalente).
- **Mesmo ambiente para todos:** todo mundo usa a mesma imagem e as mesmas variáveis de ambiente para o teste.
- **Não precisa ter Maven/Java na máquina** para rodar os testes; só Docker.

---

## 5. Configuração do serviço de teste no docker-compose

O arquivo **docker-compose.yml** pode ter um serviço **test** que:

1. Usa uma imagem com Maven (e Java).
2. Monta o código do projeto (ou usa uma imagem buildada com o código).
3. Define `DB_HOST=postgres`, `DB_PORT=5432`, `DB_NAME=confeitaria`, `DB_USER=admin`, `DB_PASSWORD=12345`.
4. Roda `mvn test`.
5. Depende do serviço **postgres** (`depends_on`).

Assim, quando você roda `docker-compose run --rm test`, o Docker:

- Sobe o Postgres (se ainda não estiver no ar).
- Cria um container temporário para o serviço **test**.
- Nesse container, executa Maven e os testes; os testes conectam em `postgres:5432`.
- Remove o container ao final (com `--rm`).

O **docker-compose.yml** do projeto já inclui o serviço **test**. Ele usa a imagem **maven:3.9-eclipse-temurin-17**, monta o diretório do projeto em `/app`, define as variáveis de ambiente para o banco (`DB_HOST=postgres`, `DB_PORT=5432`, etc.) e executa `mvn test`. O serviço depende do **postgres** (só roda depois do healthcheck).

**Comando para rodar o teste dentro do Docker:**

```bash
# Na pasta onde está o docker-compose.yml (sistemaConfeitaria)
docker-compose up -d postgres
docker-compose run --rm test
```

O `-d` deixa o Postgres em segundo plano; o `run --rm test` sobe um container temporário, roda os testes e remove o container ao final. A saída do Maven (incluindo o resultado do **UserRegistrationTest**) aparece no terminal.

---

## 6. Resumo rápido

| Onde roda      | Banco              | Como rodar                          |
|----------------|--------------------|-------------------------------------|
| Na sua máquina | Postgres no Docker | `docker-compose up -d postgres` e `mvn test` (ou pela IDE) |
| No Docker      | Postgres no Docker | `docker-compose run --rm test` (com serviço `test` no compose) |

O teste em si é o mesmo (**UserRegistrationTest.cadastrarUsuario()**); só mudam o ambiente (host vs container) e as variáveis de conexão (127.0.0.1:5434 vs postgres:5432).

---

## 7. Código completo do teste

Classe **UserRegistrationTest** (pacote `app`), em `src/test/java/app/UserRegistrationTest.java`:

```java
package app;

import model.entities.Address;
import model.entities.Area;
import model.entities.Person;
import model.entities.User;
import model.repositories.CreateTables;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste automatizado para o cadastro de usuário.
 * Fluxo: Area → Address → Person → User (ordem das dependências).
 */
class UserRegistrationTest {

    private RepositoryArea repositoryArea;
    private RepositoryAddress repositoryAddress;
    private RepositoryPerson repositoryPerson;
    private RepositoryUser repositoryUser;

    @BeforeEach
    void setUp() throws SQLException {
        CreateTables.createAllTables();
        repositoryArea = new RepositoryArea();
        repositoryAddress = new RepositoryAddress();
        repositoryPerson = new RepositoryPerson();
        repositoryUser = new RepositoryUser();
    }

    @Test
    void cadastrarUsuario() throws Exception {
        // 1. Criar Area
        Area area = new Area("Centro", 10.0);
        boolean areaCriada = repositoryArea.createArea(area);
        assertTrue(areaCriada, "Area deve ser criada");

        area = repositoryArea.findByNameArea("Centro");
        assertNotNull(area, "Area deve ser encontrada por nome");
        assertNotNull(area.getId(), "Area deve ter id após persistência");

        // 2. Criar Address (com a Area)
        Address address = new Address(area, "12345-678", "Rua das Flores", 100, "Sala 1", "Próximo ao mercado");
        boolean addressCriado = repositoryAddress.createAddress(address);
        assertTrue(addressCriado, "Address deve ser criado");

        List<Address> enderecos = repositoryAddress.findAllAddress();
        assertFalse(enderecos.isEmpty(), "Deve existir pelo menos um endereço");
        address = enderecos.get(enderecos.size() - 1);
        assertNotNull(address.getInteger(), "Address deve ter id após persistência");

        // 3. Criar Person (com o Address)
        Person person = new Person("Maria", "Silva", "maria.silva@email.com", address);
        boolean personCriada = repositoryPerson.createPerson(person);
        assertTrue(personCriada, "Person deve ser criada");

        person = repositoryPerson.findByEmailPerson("maria.silva@email.com");
        assertNotNull(person, "Person deve ser encontrada por email");
        assertNotNull(person.getId(), "Person deve ter id após persistência");

        // 4. Criar User (com o id da Person e senha)
        char[] senha = "senha123".toCharArray();
        User user = new User(person.getId(), "Maria", "Silva", "maria.silva@email.com", senha);
        boolean userCriado = repositoryUser.createUser(user);
        assertTrue(userCriado, "User deve ser criado");

        // 5. Validar: buscar usuário por email
        User encontrado = repositoryUser.findByEmailUser("maria.silva@email.com");
        assertNotNull(encontrado, "Usuário deve ser encontrado por email após cadastro");
        assertEquals("maria.silva@email.com", encontrado.getEmail(), "Email deve coincidir");
        assertEquals("Maria", encontrado.getFirstName(), "Nome deve coincidir");
        assertEquals("Silva", encontrado.getLastName(), "Sobrenome deve coincidir");
    }
}
```

**Resumo do que cada trecho faz:**

- **@BeforeEach setUp():** cria todas as tabelas e instancia os repositórios antes de cada teste.
- **cadastrarUsuario():** cria Area → busca por nome (id) → cria Address → busca último (id) → cria Person → busca por email (id) → cria User → valida com `findByEmailUser` e asserts (não nulo, email, nome, sobrenome).
