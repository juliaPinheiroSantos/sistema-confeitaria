# 🎂 Documentação do Sistema de Confeitaria

Documentação técnica gerada automaticamente via **Custom Doclet**.

Para rodar testes e usar **Docker** (PostgreSQL em container, testes no Docker), veja [testAndDocker.md](testAndDocker.md).  
**Rodar todo o projeto no Docker e ver as telas:** use o serviço `app-gui` e conecte um cliente VNC em localhost:5900 (seção **0. Rodar todo o projeto no Docker e abrir as telas** em testAndDocker.md).  
Se você usa **Docker pelo WSL**, use os comandos em bash no WSL; a seção **0.1 Docker pelo WSL** em testAndDocker.md tem o passo a passo.

**Documentação minuciosa de Views e Controllers (pt-BR):** [VIEWS-E-CONTROLLERS-DETALHADO.md](VIEWS-E-CONTROLLERS-DETALHADO.md) — cada view e cada controller explicados em detalhe (atributos, métodos, parâmetros, retornos, fluxo).

**Código linha a linha (pt-BR):** [CODIGO-LINHA-A-LINHA.md](CODIGO-LINHA-A-LINHA.md) — explicação de cada linha dos controllers e das views.

---

## Como rodar (Java normal, sem Docker)

Rodar **sem Docker**: só Java e **PostgreSQL instalado localmente** na sua máquina. O mesmo vale para **Main** e **testes**.

### Pré-requisitos

1. Instale o PostgreSQL (site oficial ou gerenciador de pacotes).
2. Crie o banco e o usuário:
   - Banco: `confeitaria`
   - Usuário: `admin`
   - Senha: `12345`
   - Porta: **5434** (ou defina `DB_PORT`).
3. Inicie o servidor PostgreSQL.

**Se aparecer "JAVA_HOME not found"** (PowerShell, só nesta sessão):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
```

(Ajuste o caminho do JDK; para deixar permanente, use **Variáveis de ambiente** do Windows.)

No diretório do projeto (onde está o `pom.xml`), use o **Maven Wrapper** (`.\mvnw.cmd`). Não use `mvn` a menos que o Maven esteja no PATH.

#### O que é o mvnw (Maven Wrapper)?

O **mvnw** (no Windows, `mvnw.cmd`) é o **Maven Wrapper**: um script que permite rodar Maven **sem ter o Maven instalado** na máquina. Na primeira execução ele baixa a versão do Maven definida no projeto (em `.mvn/wrapper/maven-wrapper.properties`) e depois usa essa versão para executar os comandos. Assim, basta ter **Java** instalado para compilar e testar o projeto.

| Se você tivesse Maven instalado | Com Maven Wrapper (este projeto) |
|--------------------------------|----------------------------------|
| `mvn compile`                  | `.\mvnw.cmd compile` (Windows) ou `./mvnw compile` (Linux/macOS) |
| `mvn test`                     | `.\mvnw.cmd test`               |
| `mvn package`                  | `.\mvnw.cmd package`            |

Os arquivos do wrapper no projeto são: `mvnw`, `mvnw.cmd` e a pasta `.mvn/wrapper/` (com a versão do Maven e o JAR do wrapper).

### Rodar o Main

```powershell
.\mvnw.cmd compile exec:java
```

Compila e executa a classe `app.Main`. Outra classe: `.\mvnw.cmd exec:java -Dexec.mainClass="outra.classe.Main"`.

### Rodar os testes

```powershell
.\mvnw.cmd test
```

Compila e executa os testes (ex.: `UserRegistrationTest`). Só a classe de teste: `.\mvnw.cmd test -Dtest=UserRegistrationTest`.

### Rodar no Visual Studio Code

1. **Instale o Java no VS Code**
   - Abra o VS Code → **Extensões** (Ctrl+Shift+X).
   - Procure por **"Extension Pack for Java"** (Microsoft) e instale (inclui Language Support, Debugger, Maven, etc.).

2. **Defina o JAVA_HOME** (se ainda não tiver)
   - No Windows: **Configurações do sistema** → **Variáveis de ambiente** → em "Variáveis do sistema" crie ou edite **JAVA_HOME** apontando para a pasta do JDK (ex.: `C:\Program Files\Java\jdk-21.0.10`).
   - Reinicie o VS Code depois de alterar variáveis de ambiente.

3. **Abra a pasta certa**
   - **Arquivo** → **Abrir pasta** e escolha a pasta **sistemaConfeitaria** (a que contém o `pom.xml` e a pasta `src`). Não abra a pasta pai do repositório.

4. **Deixe o projeto carregar**
   - A extensão Java vai ler o Maven e configurar o projeto (pode levar alguns segundos). Na barra de status, espere aparecer algo como um ícone de Java ou "Building workspace" terminar.

5. **Rode a aplicação (Main)**
   - Pressione **Ctrl+Shift+D** (ou clique no ícone **Run and Debug** na barra lateral).
   - No dropdown no topo, selecione **Main**.
   - Clique no **botão verde Play** (ou pressione **F5**).

6. **Rode os testes**
   - No mesmo painel **Run and Debug**, no dropdown selecione **UserRegistrationTest** e clique em **Play** (ou F5).
   - Ou abra `src/test/java/app/UserRegistrationTest.java` e, se aparecer **Run** em cima do método `cadastrarUsuario`, clique nele.

O projeto já tem `.vscode/launch.json` e `.vscode/settings.json` configurados. O **PostgreSQL** precisa estar rodando localmente (banco `confeitaria`, porta 5434, etc.). Se der erro de "main class" ou "JAVA_HOME", use **Ctrl+Shift+P** → **Java: Update project configuration** e, se precisar, **Java: Clean the Java language server workspace**. Mais detalhes: [Troubleshooting do Debugger for Java](https://github.com/Microsoft/vscode-java-debug/blob/master/Troubleshooting.md).

**Se aparecer "package model.repositories does not exist" (ou model.entities / services):** a compilação está rodando só o arquivo `Main.java` em vez do projeto inteiro. Faça o seguinte: (1) Abra a pasta **sistemaConfeitaria** (a que contém o `pom.xml`), não a pasta pai; (2) Use **Run and Debug** (F5) e escolha **Main** no dropdown — não use "Run Java" em cima do arquivo; (3) **Ou use a tarefa Maven:** **Terminal** → **Run Task** → **Run Main (Maven)** (compila e executa o projeto inteiro); (4) Ou no terminal, na pasta sistemaConfeitaria: `.\mvnw.cmd compile exec:java`; (5) Se continuar: **Ctrl+Shift+P** → **Java: Clean Java Language Server Workspace** → **Reload Window**.

### Rodar só clicando (IDE – Cursor ou VS Code)

Se você já está no Cursor ou no VS Code com a extensão Java instalada e a pasta **sistemaConfeitaria** aberta:

- **Painel Run and Debug (Ctrl+Shift+D):** escolha **Main** ou **UserRegistrationTest** no dropdown e clique em **Play** (F5).
- **Link em cima do método:** abra `Main.java` ou `UserRegistrationTest.java` e clique em **Run** ou **Run | Debug** em cima do `main` ou do `@Test`.

### Outras formas

| Forma | Main | Testes |
|-------|------|--------|
| **Terminal (Maven Wrapper)** | `.\mvnw.cmd compile exec:java` | `.\mvnw.cmd test` |
| **IDE (botão Run)** | Clique em Run acima de `main` em `Main.java` | Clique em Run acima do método de teste em `UserRegistrationTest.java` |

Se o Postgres estiver em outra porta/host: `$env:DB_PORT = "5432"; .\mvnw.cmd compile exec:java` ou `$env:DB_PORT = "5432"; .\mvnw.cmd test`.

---

## 📦 Pacote: `entities`

### 📄 Classe: `Address`

| Elemento    | Nome              |
| :---------- | :---------------- |
| 🔹 Atributo | `id`              |
| 🔹 Atributo | `cep`             |
| 🔹 Atributo | `street`          |
| 🔹 Atributo | `number`          |
| 🔹 Atributo | `complement`      |
| 🔹 Atributo | `reference`       |
| 🔹 Atributo | `area`            |
| ⚙️ Método   | `getInteger()`    |
| ⚙️ Método   | `getCep()`        |
| ⚙️ Método   | `setCep()`        |
| ⚙️ Método   | `getStreet()`     |
| ⚙️ Método   | `setStreet()`     |
| ⚙️ Método   | `getNumber()`     |
| ⚙️ Método   | `setNumber()`     |
| ⚙️ Método   | `getComplement()` |
| ⚙️ Método   | `setComplement()` |
| ⚙️ Método   | `getReference()`  |
| ⚙️ Método   | `setReference()`  |
| ⚙️ Método   | `setArea()`       |
| ⚙️ Método   | `getArea()`       |
| ⚙️ Método   | `toString()`      |

---

### 📄 Classe: `AddressException`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `Area`

| Elemento    | Nome         |
| :---------- | :----------- |
| 🔹 Atributo | `id`         |
| 🔹 Atributo | `name`       |
| 🔹 Atributo | `fee`        |
| ⚙️ Método   | `getId()`    |
| ⚙️ Método   | `getName()`  |
| ⚙️ Método   | `setName()`  |
| ⚙️ Método   | `getFee()`   |
| ⚙️ Método   | `setFee()`   |
| ⚙️ Método   | `toString()` |

---

### 📄 Classe: `DbException`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `DeliveryType`

| Elemento  | Nome        |
| :-------- | :---------- |
| ⚙️ Método | `values()`  |
| ⚙️ Método | `valueOf()` |

---

### 📄 Classe: `FlavorLevel`

| Elemento  | Nome        |
| :-------- | :---------- |
| ⚙️ Método | `values()`  |
| ⚙️ Método | `valueOf()` |

---

### 📄 Classe: `Order`

| Elemento    | Nome                    |
| :---------- | :---------------------- |
| 🔹 Atributo | `id`                    |
| 🔹 Atributo | `idUser`                |
| 🔹 Atributo | `dateTime`              |
| 🔹 Atributo | `totalPrice`            |
| 🔹 Atributo | `observations`          |
| 🔹 Atributo | `delivery`              |
| 🔹 Atributo | `orderItems`            |
| ⚙️ Método   | `getId()`               |
| ⚙️ Método   | `getDateTime()`         |
| ⚙️ Método   | `setDateTime()`         |
| ⚙️ Método   | `getTotalPrice()`       |
| ⚙️ Método   | `setTotalPrice()`       |
| ⚙️ Método   | `calculateTotalPrice()` |
| ⚙️ Método   | `getObservations()`     |
| ⚙️ Método   | `setObservations()`     |
| ⚙️ Método   | `getDelivery()`         |
| ⚙️ Método   | `setDelivery()`         |
| ⚙️ Método   | `getOrderItems()`       |
| ⚙️ Método   | `setOrderItems()`       |
| ⚙️ Método   | `addItem()`             |
| ⚙️ Método   | `removeAllSameItems()`  |
| ⚙️ Método   | `removeOneItem()`       |
| ⚙️ Método   | `toString()`            |

---

### 📄 Classe: `OrderItems`

| Elemento    | Nome                 |
| :---------- | :------------------- |
| 🔹 Atributo | `id`                 |
| 🔹 Atributo | `product`            |
| 🔹 Atributo | `quantity`           |
| 🔹 Atributo | `priceAtMoment`      |
| ⚙️ Método   | `getId()`            |
| ⚙️ Método   | `getProduct()`       |
| ⚙️ Método   | `setProduct()`       |
| ⚙️ Método   | `getQuantity()`      |
| ⚙️ Método   | `setQuantity()`      |
| ⚙️ Método   | `getPriceAtMoment()` |
| ⚙️ Método   | `setPriceAtMoment()` |
| ⚙️ Método   | `subtotal()`         |
| ⚙️ Método   | `toString()`         |

---

### 📄 Classe: `Person`

| Elemento    | Nome             |
| :---------- | :--------------- |
| 🔹 Atributo | `id`             |
| 🔹 Atributo | `firstName`      |
| 🔹 Atributo | `lastName`       |
| 🔹 Atributo | `email`          |
| 🔹 Atributo | `address`        |
| ⚙️ Método   | `setId()`        |
| ⚙️ Método   | `getId()`        |
| ⚙️ Método   | `getFirstName()` |
| ⚙️ Método   | `setFirstName()` |
| ⚙️ Método   | `getLastName()`  |
| ⚙️ Método   | `setLastName()`  |
| ⚙️ Método   | `getEmail()`     |
| ⚙️ Método   | `setEmail()`     |
| ⚙️ Método   | `getAddress()`   |
| ⚙️ Método   | `setAddress()`   |
| ⚙️ Método   | `toString()`     |

---

### 📄 Classe: `Product`

| Elemento    | Nome               |
| :---------- | :----------------- |
| 🔹 Atributo | `id`               |
| 🔹 Atributo | `name`             |
| 🔹 Atributo | `description`      |
| 🔹 Atributo | `price`            |
| 🔹 Atributo | `size`             |
| 🔹 Atributo | `flavor`           |
| 🔹 Atributo | `level`            |
| ⚙️ Método   | `setId()`          |
| ⚙️ Método   | `getId()`          |
| ⚙️ Método   | `getName()`        |
| ⚙️ Método   | `setName()`        |
| ⚙️ Método   | `getDescription()` |
| ⚙️ Método   | `setDescription()` |
| ⚙️ Método   | `getFlavor()`      |
| ⚙️ Método   | `setFlavor()`      |
| ⚙️ Método   | `getLevel()`       |
| ⚙️ Método   | `setLevel()`       |
| ⚙️ Método   | `getSize()`        |
| ⚙️ Método   | `setSize()`        |
| ⚙️ Método   | `getPrice()`       |
| ⚙️ Método   | `setPrice()`       |
| ⚙️ Método   | `toString()`       |

---

### 📄 Classe: `Size`

| Elemento    | Nome          |
| :---------- | :------------ |
| ⚙️ Método   | `values()`    |
| ⚙️ Método   | `valueOf()`   |
| 🔹 Atributo | `id`          |
| 🔹 Atributo | `yield`       |
| 🔹 Atributo | `weight`      |
| ⚙️ Método   | `getId()`     |
| ⚙️ Método   | `getYield()`  |
| ⚙️ Método   | `getWeight()` |

---

### 📄 Classe: `User`

| Elemento    | Nome                |
| :---------- | :------------------ |
| 🔹 Atributo | `idUser`            |
| 🔹 Atributo | `passwordHash`      |
| ⚙️ Método   | `setIdUser()`       |
| ⚙️ Método   | `getIdUser()`       |
| ⚙️ Método   | `getPasswordHash()` |
| ⚙️ Método   | `toString()`        |

---

## 📦 Pacote: `repositories`

### 📄 Classe: `CreateTables`

| Elemento  | Nome                      |
| :-------- | :------------------------ |
| ⚙️ Método | `createTablePerson()`     |
| ⚙️ Método | `createTableUser()`       |
| ⚙️ Método | `createTableArea()`       |
| ⚙️ Método | `createTableAddress()`    |
| ⚙️ Método | `createTableOrder()`      |
| ⚙️ Método | `createTableProduct()`    |
| ⚙️ Método | `createTableOrderItems()` |

---

### 📄 Classe: `DBConnection`

| Elemento    | Nome              |
| :---------- | :---------------- |
| 🔹 Atributo | `ADDRESS_IP`      |
| 🔹 Atributo | `ADDRESS_PORT`    |
| 🔹 Atributo | `NAME_DATABASE`   |
| 🔹 Atributo | `USER`            |
| 🔹 Atributo | `PASSWORD`        |
| ⚙️ Método   | `getConnection()` |

---

### 📄 Classe: `RepositoryPerson`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `RepositoryProduct`

| Elemento | Nome |
| :------- | :--- |

---

### 📄 Classe: `RepositoryUser`

| Elemento    | Nome           |
| :---------- | :------------- |
| 🔹 Atributo | `insertUser`   |
| ⚙️ Método   | `createUser()` |

---

## 📦 Pacote: `services`

### 📄 Classe: `EncryptionService`

| Elemento    | Nome              |
| :---------- | :---------------- |
| 🔹 Atributo | `ALGORITHM`       |
| 🔹 Atributo | `ITERATIONS`      |
| 🔹 Atributo | `KEY_LENGTH`      |
| ⚙️ Método   | `hashPassword()`  |
| ⚙️ Método   | `checkPassword()` |
| ⚙️ Método   | `generateHash()`  |

---

## 📦 Pacote: `app`

### 📄 Classe: `Main`

| Elemento  | Nome     |
| :-------- | :------- |
| ⚙️ Método | `main()` |

---
