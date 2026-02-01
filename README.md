# Sistema Confeitaria

Sistema em Java para gestão de uma confeitaria: cadastro de pessoas e usuários, endereços por área, produtos (sabores e tamanhos), pedidos com itens e tipo de entrega. Persistência em PostgreSQL com JDBC e execução via Docker.

---

## Visão geral

O projeto implementa a camada de modelo e persistência de um sistema de confeitaria:

- **Pessoas e usuários** — Pessoa com endereço; usuário vinculado à pessoa (login/senha).
- **Áreas e endereços** — Áreas com taxa (fee); endereços vinculados a uma área (CEP, rua, número, etc.).
- **Produtos** — Nome, sabor, nível (FlavorLevel), tamanho (Size), preço e descrição.
- **Pedidos** — Pedido com data/hora, valor total, tipo de entrega (ENTREGA/RETIRADA), observações e itens (produto, quantidade, preço no momento).

A aplicação sobe o banco (PostgreSQL em container), cria as tabelas na ordem correta das dependências e pode ser estendida para fluxos de cadastro, login e pedidos.

---

## Tecnologias

| Item | Tecnologia |
|------|------------|
| Linguagem | Java (módulo `sistemaConfeitaria`) |
| JDK | Eclipse Temurin 23 |
| Banco de dados | PostgreSQL 16 |
| Acesso a dados | JDBC (driver `postgresql-42.7.2.jar`) |
| Containers | Docker e Docker Compose |
| Documentação | Javadoc + Custom Doclet (ver `DOCUMENTACAO.md` em `sistemaConfeitaria/src/`) |

---

## Estrutura do projeto

```
sistema-confeitaria/
├── README.md                 # Este arquivo
├── .gitignore
│
└── sistemaConfeitaria/       # Projeto Java + Docker
    ├── docker-compose.yml    # Serviços: postgres e app
    ├── Dockerfile            # Build multi-stage: compila e roda app
    ├── docker-db-reset.md    # Guia para reset do banco e comandos úteis
    │
    └── src/
        ├── module-info.java
        ├── app/
        │   └── Main.java              # Ponto de entrada: CreateTables.createAllTables()
        ├── model/
        │   ├── entities/              # Entidades de domínio
        │   │   ├── Address, AddressException, Area
        │   │   ├── Person, User
        │   │   ├── Product, Order, OrderItems
        │   │   ├── DeliveryType, FlavorLevel, Size
        │   │   └── ...
        │   └── repositories/          # Acesso ao banco
        │       ├── DBConnection       # Conexão JDBC (env ou padrão)
        │       ├── CreateTables       # Criação das tabelas na ordem das FKs
        │       ├── RepositoryUser, RepositoryPerson, RepositoryProduct
        │       └── ...
        ├── services/
        │   └── EncryptionService      # Hash de senha (usuário)
        ├── com/confeitaria/infra/doclet/
        │   └── ListaDoclet.java       # Doclet para documentação
        ├── DOCUMENTACAO.md             # Documentação gerada (entidades/repositórios)
        └── repository.md              # Detalhes dos repositórios
```

---

## Pré-requisitos

- **Docker** e **Docker Compose** (para rodar banco e app em containers).
- Ou: **JDK 23** + **PostgreSQL 16** (para rodar só a aplicação na máquina).

---

## Como rodar com Docker (recomendado)

1. Entre na pasta do projeto Java/Docker:

   ```bash
   cd sistemaConfeitaria
   ```

2. Suba os serviços (PostgreSQL + app). A app espera o Postgres ficar saudável e depois cria as tabelas:

   ```bash
   docker compose up -d --build
   ```

3. Verifique os logs da aplicação (confirmação de criação das tabelas):

   ```bash
   docker compose logs app
   ```

**Ambiente no Docker:**

- Banco: `confeitaria` | Usuário: `admin` | Senha: `12345`
- Postgres: container `confeitaria-postgres`, porta no host `5434`
- App: container `confeitaria-app`

Para **resetar o banco** (zerar dados e recriar tabelas), use os comandos descritos em **sistemaConfeitaria/docker-db-reset.md**.

---

## Como rodar sem Docker (apenas app local)

1. Tenha o PostgreSQL 16 rodando com um banco `confeitaria` e usuário/senha (ex.: `admin`/`12345`), na porta **5434** (ou ajuste em `DBConnection` / variáveis de ambiente).

2. Coloque o JAR do driver JDBC do PostgreSQL no classpath e compile/execute a partir da pasta `sistemaConfeitaria` (onde está o `src/`):

   ```bash
   cd sistemaConfeitaria
   javac -cp ".:postgresql.jar" -d bin $(find src -name "*.java")
   java -cp ".:bin:postgresql.jar" app.Main
   ```

   No Windows (PowerShell), troque `:` por `;` no classpath se necessário.

3. Variáveis de ambiente opcionais (se não usar, `DBConnection` usa localhost:5434, banco `confeitaria`, `admin`/`12345`):

   - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`

---

## Reset do banco e comandos úteis

O arquivo **sistemaConfeitaria/docker-db-reset.md** contém:

- Listagem de tabelas (`psql` / `\dt`)
- Dropar apenas a tabela `user` (ou outras)
- Recriar todo o banco (`docker compose down -v` + `up -d --build`)
- Uso do `psql` interativo e rebuild da app

Consulte esse guia para qualquer operação de reset ou inspeção do banco no Docker.

---

## Documentação adicional

- **sistemaConfeitaria/src/DOCUMENTACAO.md** — Documentação técnica (entidades, repositórios) gerada via Custom Doclet.
- **sistemaConfeitaria/src/repository.md** — Detalhes dos repositórios e persistência.

---

## Licença e uso

Projeto de estudo (“My First Software”). Sinta-se à vontade para usar e adaptar.
