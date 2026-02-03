# Conceitos e lógica – Controllers e Views

Documentação em português (pt-BR) que explica **conceitos**, **lógica** e **código** do Sistema de Confeitaria, separada por **controller** e por **view**.

---

## Índice

1. [Conceitos gerais](#1-conceitos-gerais)
2. [Controllers](#2-controllers)
   - [ControllerHome](#21-controllerhome)
   - [ControllerCadastro](#22-controllercadastro)
   - [ControllerLogin](#23-controllerlogin)
3. [Views](#3-views)
   - [ViewTheme](#31-viewtheme)
   - [ViewHome](#32-viewhome)
   - [ViewCadastro](#33-viewcadastro)
   - [ViewLogin](#34-viewlogin)
   - [ViewDashboard](#35-viewdashboard)

---

## 1. Conceitos gerais

### 1.1 MVC (Model-View-Controller)

- **Model:** dados e regras de negócio (entidades, repositórios). Não sabe nada de janelas ou botões.
- **View:** o que o usuário vê (JFrame, botões, campos). Só desenha a tela e repassa cliques para o controller.
- **Controller:** recebe o que o usuário fez (clicou em “Cadastrar”, enviou o formulário), decide o que fazer e usa o Model. Não desenha nada.

**Por que separar?** Assim a lógica (controller) não fica misturada com a tela (view), fica mais fácil de testar e de mudar uma parte sem quebrar a outra.

### 1.2 Injeção de dependência

Em vez de cada controller criar seus repositórios com `new RepositoryPerson()`, eles **recebem** os repositórios pelo construtor. Quem monta tudo é o `Main` (bootstrap).

**Vantagens:** os controllers não dependem de implementações concretas; em testes você pode passar “repositórios falsos”; fica claro de onde vêm as dependências.

### 1.3 Callback (Runnable / Consumer)

Quando uma tela fecha (Cadastro ou Login), quem abriu precisa ser avisado para mostrar a tela principal de novo. Em vez da view chamar o controller diretamente por nome, recebemos um **callback**: uma função que a view chama ao fechar (`onClose.run()` ou `onLoginSuccess.accept(this)`). Assim a view não precisa conhecer o ControllerHome; só executa “o que me passaram para fazer ao fechar”.

---

## 2. Controllers

Cada controller concentra a **lógica** de uma parte do fluxo: navegação (Home), cadastro (Cadastro) ou login (Login).

---

### 2.1 ControllerHome

**Responsabilidade:** coordenar a **navegação** entre a tela inicial (ViewHome), a tela de cadastro (ViewCadastro), a de login (ViewLogin) e o dashboard (ViewDashboard).

**Conceito:** ele não desenha nada; só esconde/mostra janelas e cria novas telas quando o usuário clica em “Cadastrar” ou “Entrar”. Recebe a ViewHome e os outros dois controllers (Cadastro e Login) por injeção.

#### Construtor

```java
public ControllerHome(ViewHome viewHome, ControllerCadastro controllerCadastro, ControllerLogin controllerLogin) {
    this.viewHome = viewHome;
    this.controllerCadastro = controllerCadastro;
    this.controllerLogin = controllerLogin;
}
```

**Lógica:** guarda as referências. A ViewHome será escondida quando abrirmos Cadastro ou Login e mostrada de novo quando essas telas fecharem. Os controllers de Cadastro e Login são usados ao construir as respectivas views (ViewCadastro e ViewLogin precisam deles).

#### openRegister()

```java
public void openRegister() {
    FlowHandler.log("HOME_HIDDEN");
    viewHome.setVisible(false);
    FlowHandler.log("REGISTER_OPENED");
    ViewCadastro viewCadastro = new ViewCadastro(controllerCadastro, this::backToHome);
    viewCadastro.setLocationRelativeTo(viewHome);
    viewCadastro.setVisible(true);
}
```

**Lógica:**

1. Registra no fluxo que a home foi escondida.
2. Esconde a ViewHome (`setVisible(false)`), para não ficar duas janelas “principais” ao mesmo tempo.
3. Cria a ViewCadastro passando: o controller de cadastro (para o botão “Cadastrar”) e o callback `this::backToHome` (para quando fechar a tela de cadastro, voltar à home).
4. Posiciona a nova janela em relação à ViewHome (centralizada em cima dela).
5. Mostra a ViewCadastro.

Assim, ao clicar em “Cadastrar” na home, a home some e só a tela de cadastro aparece; ao fechar (Voltar ou X), `onClose` chama `backToHome()` e a home volta.

#### openLogin()

```java
public void openLogin() {
    FlowHandler.log("HOME_HIDDEN");
    viewHome.setVisible(false);
    FlowHandler.log("LOGIN_OPENED");
    ViewLogin viewLogin = new ViewLogin(controllerLogin, this::backToHome, this::onLoginSuccess);
    viewLogin.setLocationRelativeTo(viewHome);
    viewLogin.setVisible(true);
}
```

**Lógica:** igual à do cadastro, mas a ViewLogin recebe dois callbacks: um para “voltar” (`backToHome`) e outro para “login com sucesso” (`onLoginSuccess`). No sucesso, não queremos só voltar à home; queremos fechar a tela de login e abrir o dashboard.

#### backToHome()

```java
public void backToHome() {
    FlowHandler.log("BACK_TO_HOME");
    SwingUtilities.invokeLater(() -> viewHome.setVisible(true));
}
```

**Lógica:** é o callback chamado quando o usuário fecha a tela de Cadastro ou de Login (Voltar ou X). Usamos `SwingUtilities.invokeLater` para mostrar a ViewHome na **thread do Swing** (EDT), evitando problemas se o callback for chamado de outra thread.

#### onLoginSuccess(ViewLogin viewLogin)

```java
public void onLoginSuccess(ViewLogin viewLogin) {
    FlowHandler.log("DASHBOARD_OPENED");
    viewLogin.dispose();
    ViewDashboard dashboard = new ViewDashboard();
    dashboard.setLocationRelativeTo(viewLogin);
    dashboard.setVisible(true);
}
```

**Lógica:** chamado quando o login é válido. Fecha a tela de login (`dispose()`), cria a ViewDashboard, posiciona e exibe. A partir daí o usuário está “dentro” do sistema (área logada).

---

### 2.2 ControllerCadastro

**Responsabilidade:** validar os dados do cadastro, verificar e-mail duplicado e **persistir** (endereço → person → user) no banco. Não conhece janelas; só recebe dados e retorna mensagem de erro ou sucesso.

**Conceito:** o fluxo de cadastro exige **ordem** no banco: primeiro Address (que depende de Area), depois Person (depende de Address), depois User (depende de Person). Por isso existem métodos nos repositórios que retornam o **id** gerado após o `INSERT` (`createAddressAndReturnId`, `createPersonAndReturnId`).

#### Construtor e dependências

```java
public ControllerCadastro(RepositoryPerson repoPerson, RepositoryUser repoUser,
                          RepositoryAddress repoAddress, RepositoryArea repoArea) {
    this.repoPerson = repoPerson;
    this.repoUser = repoUser;
    this.repoAddress = repoAddress;
    this.repoArea = repoArea;
}
```

**Lógica:** todos os repositórios são injetados. O controller usa `repoPerson` para verificar e-mail e criar person, `repoUser` para criar user, `repoAddress` para criar endereço, `repoArea` para listar áreas e buscar área por id.

#### isEmailAlreadyRegistered(String email)

```java
public boolean isEmailAlreadyRegistered(String email) {
    try {
        return repoPerson.findByEmailPerson(email) != null;
    } catch (SQLException e) {
        e.printStackTrace();
        return true;  // em dúvida, evita duplicar
    }
}
```

**Lógica:** consulta se já existe person com aquele e-mail. Se der erro de banco, retorna `true` para não arriscar cadastrar duplicado.

#### listAreas()

```java
public List<Area> listAreas() throws SQLException {
    return repoArea.findAllArea();
}
```

**Lógica:** devolve a lista de áreas para a view preencher o combo (bairros de Anápolis-GO, por exemplo).

#### register(...) – fluxo geral

```java
public String register(String firstName, String lastName, String email, char[] password,
                        Integer idArea, String street, Integer number, String cep,
                        String complement, String reference) {
    FlowHandler.log("REGISTER_SUBMIT");
    Optional<String> validationError = validateRequiredFields(firstName, email, password, idArea, street);
    if (validationError.isPresent()) {
        clearPassword(password);
        FlowHandler.log("REGISTER_ERROR", validationError.get());
        return validationError.get();
    }
    // ... verificação de e-mail duplicado e área ...
    // ... persistUser(...) ...
    FlowHandler.log("REGISTER_SUCCESS");
    return null;  // null = sucesso
}
```

**Lógica:**

1. **Validação:** campos obrigatórios (nome, e-mail, senha, área, rua). Se faltar algo, retorna a mensagem e não persiste. `clearPassword` zera o array de senha por segurança.
2. **E-mail duplicado:** consulta de novo no banco; se já existir, retorna mensagem e não persiste.
3. **Área:** confirma que o `idArea` existe no banco.
4. **Persistência:** chama `persistUser`, que cria Address → Person → User na ordem. Se qualquer passo falhar, retorna mensagem de erro.
5. Retorno: **`null` = sucesso**; **`String` = mensagem de erro** para a view exibir em `JOptionPane`.

#### validateRequiredFields(...)

**Lógica:** verifica nome, e-mail, senha, idArea e rua. Usa `Optional<String>`: se `Optional.empty()`, está tudo certo; se `Optional.of(mensagem)`, retorna essa mensagem para o usuário.

#### persistUser(...)

**Lógica (ordem no banco):**

1. Monta o **Address** (área, rua, número, CEP, etc.) e chama `repoAddress.createAddressAndReturnId(address)` → obtém o `id` do endereço.
2. Cria a **Person** com esse endereço e chama `repoPerson.createPersonAndReturnId(person)` → obtém o `id` da person.
3. Cria o **User** (senha é hasheada no construtor de `User`) e chama `repoUser.createUser(user)`.

Se qualquer passo retornar `null` ou `false`, o método retorna `Optional.of(mensagem)`; senão retorna `Optional.empty()` (sucesso).

---

### 2.3 ControllerLogin

**Responsabilidade:** tentar **login** com e-mail e senha: buscar usuário por e-mail e validar a senha com `EncryptionService.checkPassword`. Retorna o `User` logado ou `null`.

**Conceito:** não desenha nada; só recebe credenciais e devolve o usuário ou falha. A view decide o que fazer (mostrar mensagem de erro ou chamar o callback de sucesso).

#### login(String email, char[] password)

```java
public User login(String email, char[] password) {
    FlowHandler.log("LOGIN_ATTEMPT");
    if (isEmptyCredentials(email, password)) return null;
    try {
        User user = repoUser.findByEmailUser(email.trim());
        boolean ok = user != null && EncryptionService.checkPassword(password, user.getPasswordHash());
        if (ok) FlowHandler.log("LOGIN_OK");
        else FlowHandler.log("LOGIN_FAILED");
        return ok ? user : null;
    } catch (SQLException e) { ... }
    catch (Exception e) { ... }
}
```

**Lógica:**

1. Se e-mail ou senha estiverem vazios, retorna `null`.
2. Busca o usuário pelo e-mail no banco.
3. Se existir usuário, compara a senha digitada com o hash guardado (`EncryptionService.checkPassword`). Se bater, retorna o `User`; senão retorna `null`.
4. Em caso de exceção (banco ou criptografia), registra no fluxo e retorna `null`.

A view usa: se `user != null`, chama `onLoginSuccess.accept(this)`; senão mostra “E-mail ou senha incorretos”.

---

## 3. Views

Cada view é uma **janela** (JFrame) que monta a tela em **métodos** (ex.: `configureFrame`, `buildMainPanel`, `buildTitleSection`). Todas usam o **ViewTheme** para cores e fontes.

---

### 3.1 ViewTheme

**Responsabilidade:** centralizar **cores** e **fontes** do sistema (paleta “confeitaria”: creme, dourado, marrom) e oferecer métodos que criam **painéis**, **rótulos**, **botões** e **campos** já com o estilo aplicado.

**Conceito:** assim não repetimos código em toda view e, se mudar o tema, altera em um só lugar.

#### Cores e fontes (constantes)

- **BACKGROUND:** fundo creme (#FFF9F5).
- **ACCENT:** botão primário (dourado).
- **TEXT / TEXT_MUTED:** texto principal e secundário.
- **BORDER:** bordas suaves.
- **FONT_TITLE, FONT_SUBTITLE, FONT_LABEL, FONT_BUTTON:** Segoe UI em tamanhos e pesos diferentes.

#### Métodos estáticos

- **createPanel(margens):** painel com fundo do tema e borda (margem).
- **createTitleLabel / createSubtitleLabel / createFieldLabel:** rótulos com fonte e cor do tema.
- **createPrimaryButton / createSecondaryButton:** botão de destaque (ex.: Cadastrar) e botão secundário (ex.: Voltar).
- **createTextField / createPasswordField:** campo de texto/senha com borda e fundo do tema.
- **createSection(título):** painel de seção com uma linha embaixo do título (ex.: “Dados pessoais”, “Endereço”).

**Lógica:** cada método cria o componente, aplica fonte/cor/borda e devolve. As views só chamam esses métodos em vez de configurar tudo na mão.

---

### 3.2 ViewHome

**Responsabilidade:** mostrar a **tela inicial** (título “Sistema de Confeitaria”, botões “Cadastrar” e “Entrar”) e repassar os cliques para o **ControllerHome**.

**Conceito:** o controller é injetado **depois** da construção da janela (via `setController`), porque o `Main` precisa criar primeiro a ViewHome e o ControllerHome para então ligar um no outro.

#### Construtor

```java
public ViewHome() {
    configureFrame();
    setContentPane(buildMainPanel());
}
```

**Lógica:** só faz duas coisas: configurar a janela (título, tamanho, fechamento) e definir o conteúdo como o painel principal. O restante fica em métodos menores.

#### configureFrame()

Define: título, `EXIT_ON_CLOSE`, tamanho (420x320), posição (80, 80), não redimensionável, fundo do tema, estado normal (não minimizado). **Lógica:** tudo que é configuração da **janela** fica aqui.

#### buildMainPanel()

Cria um painel com `ViewTheme.createPanel`, usa `BoxLayout` na vertical e adiciona: seção de título, espaço, seção de botões. **Lógica:** estrutura geral da tela em um único método.

#### buildTitleSection()

Monta um painel com o título “Sistema de Confeitaria” e o subtítulo “Cadastre-se ou entre para continuar”, centralizados e com fonte do tema. **Lógica:** todo o “cabeçalho” da tela inicial fica isolado.

#### buildButtonsSection()

Cria os botões “Cadastrar” (primário) e “Entrar” (secundário), tamanho máximo 220x44, centralizados. Em cada um, o **actionListener** chama o controller: `controller.openRegister()` e `controller.openLogin()`. **Lógica:** a view não decide o que fazer ao clicar; só delega ao controller.

#### setController(ControllerHome controller)

Guarda o controller para ser usado nos listeners dos botões. Chamado pelo `Main` após criar o `ControllerHome`.

---

### 3.3 ViewCadastro

**Responsabilidade:** tela de **cadastro** com formulário (dados pessoais + endereço), combo de áreas, botões Voltar e Cadastrar. Ao enviar, chama o **ControllerCadastro.register**; se houver erro, mostra `JOptionPane` e não fecha; se sucesso, mostra mensagem e fecha (o callback `onClose` é chamado pelo `WindowListener` ao dar `dispose()`).

**Conceito:** a view só coleta os dados dos campos e manda para o controller; a **validação** e a **persistência** ficam no controller. O **onClose** é um `Runnable` passado pelo ControllerHome para “voltar à home” quando a janela for fechada.

#### Construtor e callbacks

```java
public ViewCadastro(ControllerCadastro controller, Runnable onClose) {
    this.controller = controller;
    this.onClose = onClose;
    configureFrame();
    setContentPane(buildMainPanel());
}
```

**Lógica:** guarda o controller (para `register` e `listAreas`) e o callback `onClose`. Configura a janela e adiciona um **WindowListener** em `configureFrame`: quando a janela for fechada (`windowClosed`), chama `onClose.run()` para o ControllerHome mostrar a ViewHome de novo.

#### configureFrame()

Título “Cadastrar - Sistema de Confeitaria”, `DISPOSE_ON_CLOSE` (só esta janela fecha, o app continua), tamanho 440x560, fundo do tema, listener de janela fechada chamando `onClose.run()`.

#### buildMainPanel()

Retorna um **JScrollPane** em volta de um painel que contém, na ordem: título, seção “Dados pessoais”, seção “Endereço”, botões. **Lógica:** formulário longo; o scroll permite rolar se a tela for pequena.

#### buildPersonalSection() / buildAddressSection()

Cada um usa `ViewTheme.createSection("Dados pessoais")` ou `("Endereço")` e preenche com linhas de campo (rótulo + campo) via `addFieldRow`. No endereço há ainda o **combo de áreas** (`buildAreaCombo()`), que chama `controller.listAreas()` e monta um `JComboBox<Area>` com um **renderer** que mostra `area.getName()` na lista.

**Lógica:** seções separadas deixam claro o que é “pessoal” e o que é “endereço”; o combo usa o controller para buscar as áreas do banco.

#### buildAreaCombo()

Chama `controller.listAreas()`. Se der exceção, usa lista vazia. Cria o `JComboBox`, aplica fonte e borda do tema e define um **ListCellRenderer** para que cada item (objeto `Area`) seja exibido como `a.getName()`.

#### addFieldRow(labelText, field)

Cria uma linha com rótulo em cima e o campo embaixo (`BorderLayout`). **Lógica:** evita repetir o mesmo layout para cada campo.

#### onRegisterClick()

1. Lê todos os campos (nome, sobrenome, e-mail, senha, área selecionada, rua, número, CEP, complemento, referência).
2. Converte o número da casa com `parseIntOrNull` (se vazio ou inválido, fica `null`).
3. Chama `controller.register(...)` com esses valores.
4. Se o retorno for uma **String** (erro), mostra `JOptionPane` com a mensagem e **não** fecha a tela.
5. Se o retorno for **null** (sucesso), mostra “Cadastro realizado com sucesso!” e chama `dispose()`. Ao fechar, o `WindowListener` chama `onClose.run()` e a home reaparece.

**Lógica:** a view não valida nem grava; só entrega os dados e reage ao retorno do controller.

#### onBackClick()

Chama `dispose()` e `onClose.run()`. **Lógica:** Voltar fecha a janela e avisa o ControllerHome para mostrar a ViewHome.

---

### 3.4 ViewLogin

**Responsabilidade:** tela de **login** (e-mail e senha, botões Voltar e Entrar). Ao clicar em Entrar, chama o **ControllerLogin.login**; se retornar `User`, chama o callback **onLoginSuccess** (que no ControllerHome fecha a login e abre o dashboard); senão mostra “E-mail ou senha incorretos”.

**Conceito:** recebe dois callbacks: um para “voltar” (mostrar home) e outro para “login com sucesso” (abrir dashboard). Assim a view não conhece o ControllerHome; só executa o que foi combinado.

#### Construtor

```java
public ViewLogin(ControllerLogin controller, Runnable onBack, Consumer<ViewLogin> onLoginSuccess) {
    this.controller = controller;
    this.onBack = onBack;
    this.onLoginSuccess = onLoginSuccess;
    configureFrame();
    setContentPane(buildMainPanel());
}
```

**Lógica:** `onBack` é chamado quando o usuário clica em Voltar ou fecha a janela (no `WindowListener`). `onLoginSuccess` é um `Consumer<ViewLogin>`: recebe a própria ViewLogin para o ControllerHome poder dar `dispose()` nela antes de abrir o dashboard.

#### configureFrame()

Semelhante ao cadastro: título “Entrar - Sistema de Confeitaria”, `DISPOSE_ON_CLOSE`, tamanho 400x300, e **WindowListener** que chama `onBack.run()` quando a janela for fechada (Voltar ou X).

#### buildMainPanel() / buildTitleSection() / buildFieldsSection() / buildButtonsSection()

Montam: título “Entrar”, campos de e-mail e senha (com `ViewTheme`), botões Voltar e Entrar. **Lógica:** mesma ideia das outras views: dividir em métodos que montam pedaços da tela.

#### onLoginClick()

1. Lê e-mail e senha.
2. Chama `controller.login(email, password)`.
3. Se retornar **null**, mostra `JOptionPane` “E-mail ou senha incorretos” e não fecha.
4. Se retornar **User**, chama `onLoginSuccess.accept(this)`. O ControllerHome, ao receber isso, faz `viewLogin.dispose()` e abre a ViewDashboard.

**Lógica:** a view não sabe como abrir o dashboard; só informa “login deu certo” passando a própria referência para o controller decidir o que fazer.

#### onBackClick()

`dispose()` e `onBack.run()` — volta à home.

---

### 3.5 ViewDashboard

**Responsabilidade:** tela **interna** (área logada): título “Área interna” e mensagem “Você entrou no sistema. Em breve: pedidos, produtos e mais.” É um placeholder para futuras funcionalidades.

**Conceito:** não recebe controller; é aberta diretamente pelo ControllerHome após login bem-sucedido. Usa `EXIT_ON_CLOSE`: fechar esta janela encerra o aplicativo.

#### Métodos

- **configureFrame():** título, tamanho 520x360, fundo do tema, `EXIT_ON_CLOSE`.
- **buildMainPanel():** painel com seção de boas-vindas e seção de mensagem.
- **buildWelcomeSection():** rótulo “Área interna” (título do tema).
- **buildMessageSection():** rótulo com o texto explicativo.

**Lógica:** estrutura simples; só organizada em métodos para manter o padrão das outras views e facilitar futuras alterações (ex.: adicionar menu ou lista de pedidos).

---

## Resumo

| Camada   | Responsabilidade principal |
|----------|----------------------------|
| **ControllerHome** | Navegação: esconder/mostrar ViewHome, abrir ViewCadastro, ViewLogin e ViewDashboard, e callbacks de “voltar” e “login OK”. |
| **ControllerCadastro** | Validar e persistir cadastro (Address → Person → User); retornar mensagem de erro ou sucesso. |
| **ControllerLogin** | Buscar usuário por e-mail e validar senha; retornar `User` ou `null`. |
| **ViewTheme** | Cores, fontes e componentes padronizados para todas as views. |
| **ViewHome** | Tela inicial com dois botões que delegam ao ControllerHome. |
| **ViewCadastro** | Formulário de cadastro; coleta dados, chama controller e reage ao retorno; usa callback ao fechar. |
| **ViewLogin** | Formulário de login; chama controller e usa callbacks para “voltar” e “sucesso”. |
| **ViewDashboard** | Tela interna (placeholder) após login. |

Toda a **lógica** de negócio e navegação está nos **controllers**; as **views** só montam a interface e repassam ações para os controllers, usando **callbacks** quando precisam avisar “fechei” ou “login OK”.
