# Views e Controllers – Documentação minuciosa

Documentação em português (pt-BR) que descreve **cada view** e **cada controller** do Sistema de Confeitaria com nível de detalhe máximo: atributos, métodos, parâmetros, retornos, fluxo e relações entre camadas.

---

## Índice

1. [Controllers](#1-controllers)
   - [ControllerHome](#11-controllerhome)
   - [ControllerCadastro](#12-controllercadastro)
   - [ControllerLogin](#13-controllerlogin)
2. [Views](#2-views)
   - [ViewTheme](#21-viewtheme)
   - [ViewHome](#22-viewhome)
   - [ViewCadastro](#23-viewcadastro)
   - [ViewLogin](#24-viewlogin)
   - [ViewDashboard](#25-viewdashboard)

---

## 1. Controllers

---

### 1.1 ControllerHome

**Pacote:** `controller`  
**Responsabilidade:** Coordenar a **navegação** entre a tela inicial (ViewHome), a tela de cadastro (ViewCadastro), a de login (ViewLogin) e o dashboard (ViewDashboard). Não contém regras de negócio nem acesso a banco; apenas esconde/mostra janelas e instancia outras views.

#### Atributos (campos)

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `viewHome` | `ViewHome` | Referência à janela principal. É escondida ao abrir Cadastro ou Login e mostrada de novo ao fechar essas telas. |
| `controllerCadastro` | `ControllerCadastro` | Usado para criar a ViewCadastro (ela precisa do controller no construtor). |
| `controllerLogin` | `ControllerLogin` | Usado para criar a ViewLogin (ela precisa do controller no construtor). |

Todos são `final` e definidos no construtor (injeção de dependência).

#### Construtor

```java
public ControllerHome(ViewHome viewHome, ControllerCadastro controllerCadastro, ControllerLogin controllerLogin)
```

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `viewHome` | `ViewHome` | A tela inicial já construída (normalmente pelo `Main`). |
| `controllerCadastro` | `ControllerCadastro` | Controller de cadastro já instanciado (com repositórios injetados). |
| `controllerLogin` | `ControllerLogin` | Controller de login já instanciado. |

**Lógica:** Apenas atribui os três parâmetros aos campos. Quem monta e injeta é o bootstrap (`Main`).

#### Método: `openRegister()`

- **Assinatura:** `public void openRegister()`
- **Quando é chamado:** Pelo botão "Cadastrar" na ViewHome (listener chama `controller.openRegister()`).

**Passo a passo:**

1. `FlowHandler.log("HOME_HIDDEN")` — registra no fluxo que a home será escondida.
2. `viewHome.setVisible(false)` — esconde a janela principal para não haver duas “telas iniciais” ao mesmo tempo.
3. `FlowHandler.log("REGISTER_OPENED")` — registra abertura da tela de cadastro.
4. `new ViewCadastro(controllerCadastro, this::backToHome)` — cria a ViewCadastro passando:
   - o controller de cadastro (para validação e persistência);
   - o callback `this::backToHome` (Runnable) para quando a janela de cadastro for fechada (Voltar ou X).
5. `viewCadastro.setLocationRelativeTo(viewHome)` — posiciona a nova janela em relação à ViewHome (centralizada sobre ela; como a home está invisível, acaba centralizada na tela).
6. `viewCadastro.setVisible(true)` — exibe a tela de cadastro.

**Resultado:** O usuário vê apenas a tela de cadastro. Ao fechar (Voltar ou X), o `WindowListener` da ViewCadastro chama `onClose.run()`, que é `backToHome()`, e a ViewHome volta a aparecer.

#### Método: `openLogin()`

- **Assinatura:** `public void openLogin()`
- **Quando é chamado:** Pelo botão "Entrar" na ViewHome.

**Passo a passo:**

1. `FlowHandler.log("HOME_HIDDEN")`.
2. `viewHome.setVisible(false)`.
3. `FlowHandler.log("LOGIN_OPENED")`.
4. `new ViewLogin(controllerLogin, this::backToHome, this::onLoginSuccess)` — cria a ViewLogin com:
   - `controllerLogin`: para chamar `login(email, password)`;
   - `this::backToHome`: callback ao fechar (Voltar ou X);
   - `this::onLoginSuccess`: callback quando o login for bem-sucedido (Consumer que recebe a própria ViewLogin).
5. `viewLogin.setLocationRelativeTo(viewHome)`.
6. `viewLogin.setVisible(true)`.

**Resultado:** Tela de login visível. Voltar/X → `backToHome()`. Login OK → a ViewLogin chama `onLoginSuccess.accept(this)` e o ControllerHome executa `onLoginSuccess(ViewLogin)`.

#### Método: `backToHome()`

- **Assinatura:** `public void backToHome()`
- **Quando é chamado:** 
  - Pela ViewCadastro ao fechar (WindowListener ou botão Voltar) — `onClose.run()` é `backToHome`;
  - Pela ViewLogin ao fechar (WindowListener ou botão Voltar) — `onBack.run()` é `backToHome`.

**Passo a passo:**

1. `FlowHandler.log("BACK_TO_HOME")`.
2. `SwingUtilities.invokeLater(() -> viewHome.setVisible(true))` — torna a ViewHome visível na **thread do Swing (EDT)**. Isso evita problemas se o callback tiver sido disparado a partir de outra thread (ex.: ao fechar janela).

**Resultado:** A tela inicial volta a ser exibida.

#### Método: `onLoginSuccess(ViewLogin viewLogin)`

- **Assinatura:** `public void onLoginSuccess(ViewLogin viewLogin)`
- **Quando é chamado:** Pela ViewLogin quando `controller.login(...)` retorna um `User` não nulo; a view chama `onLoginSuccess.accept(this)`.

**Passo a passo:**

1. `FlowHandler.log("DASHBOARD_OPENED")`.
2. `viewLogin.dispose()` — fecha e libera a janela de login.
3. `new ViewDashboard()` — cria a tela interna (área logada).
4. `dashboard.setLocationRelativeTo(viewLogin)` — posiciona em relação à janela que está sendo fechada.
5. `dashboard.setVisible(true)` — exibe o dashboard.

**Resultado:** O usuário fica apenas na tela do dashboard. A ViewHome continua invisível; fechar o dashboard encerra o app (EXIT_ON_CLOSE).

---

### 1.2 ControllerCadastro

**Pacote:** `controller`  
**Responsabilidade:** Validar os dados do cadastro, verificar e-mail duplicado e **persistir** no banco na ordem: Address → Person → User. Não conhece janelas; só recebe dados e retorna mensagem de erro (String) ou sucesso (null).

#### Atributos (campos)

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `repoPerson` | `RepositoryPerson` | Busca person por e-mail e cria person (retorna ID). |
| `repoUser` | `RepositoryUser` | Cria usuário (login) vinculado à person. |
| `repoAddress` | `RepositoryAddress` | Cria endereço e retorna ID. |
| `repoArea` | `RepositoryArea` | Lista áreas e busca área por ID. |

Todos são `final` e injetados pelo construtor.

#### Construtor

```java
public ControllerCadastro(RepositoryPerson repoPerson, RepositoryUser repoUser,
                          RepositoryAddress repoAddress, RepositoryArea repoArea)
```

Apenas atribui os quatro repositórios aos campos. Quem instancia é o `Main`.

#### Método: `isEmailAlreadyRegistered(String email)`

- **Assinatura:** `public boolean isEmailAlreadyRegistered(String email)`
- **Retorno:** `true` se já existir person com esse e-mail ou se ocorrer exceção; `false` caso contrário.
- **Uso no projeto:** Atualmente a ViewCadastro não chama este método antes de enviar; a verificação é feita dentro de `register()`. O método existe para uso futuro ou em outras camadas.

**Passo a passo:**

1. `repoPerson.findByEmailPerson(email)` dentro de try.
2. Retorna `result != null`.
3. Em `SQLException`: imprime stack, retorna `true` (em dúvida, evita duplicar).

#### Método: `listAreas()`

- **Assinatura:** `public List<Area> listAreas() throws SQLException`
- **Retorno:** Lista de todas as áreas (ex.: bairros de Anápolis-GO com taxa de entrega).
- **Chamado por:** ViewCadastro em `buildAreaCombo()` ao montar o JComboBox de áreas.

**Passo a passo:** `return repoArea.findAllArea();`

#### Método: `register(...)`

- **Assinatura:**
```java
public String register(String firstName, String lastName, String email, char[] password,
                       Integer idArea, String street, Integer number, String cep,
                       String complement, String reference)
```
- **Retorno:** `null` = sucesso; `String` = mensagem de erro para a view exibir (ex.: em JOptionPane).

**Passo a passo detalhado:**

1. **Log:** `FlowHandler.log("REGISTER_SUBMIT")`.

2. **Validação de campos obrigatórios:**  
   `validateRequiredFields(firstName, email, password, idArea, street)`  
   - Nome em branco → "First name is required."  
   - E-mail em branco → "Email is required."  
   - Senha nula ou vazia → "Password is required."  
   - idArea nulo → "Please select an area."  
   - Rua em branco → "Street is required."  
   Se houver erro: `clearPassword(password)`, log "REGISTER_ERROR" com a mensagem, retorna a mensagem.

3. **E-mail duplicado:**  
   `repoPerson.findByEmailPerson(email.trim()) != null`  
   Se já existir: limpa senha, log "REGISTER_ERROR" ("email_already_registered"), retorna "Email already registered. Use another email or log in."

4. **Área válida:**  
   `Area area = repoArea.findByIdArea(idArea)`  
   Se `area == null`: limpa senha, log "REGISTER_ERROR" ("invalid_area"), retorna "Invalid area."

5. **Persistência:**  
   Chama `persistUser(firstName, lastName, email, password, area, street, number, cep, complement, reference)`.  
   - Se retornar `Optional.of(mensagem)`: limpa senha, log "REGISTER_ERROR", retorna a mensagem.  
   - Se retornar `Optional.empty()`: limpa senha, log "REGISTER_SUCCESS", retorna `null`.

6. **Exceções:**  
   Em `SQLException` ou `Exception`: imprime stack, limpa senha, log de erro, retorna mensagem amigável ("Database error: ..." ou "Error: ...").

#### Método privado: `validateRequiredFields(...)`

- **Assinatura:**  
  `private static Optional<String> validateRequiredFields(String firstName, String email, char[] password, Integer idArea, String street)`
- **Retorno:** `Optional.empty()` se tudo OK; `Optional.of(mensagem)` com a primeira mensagem de erro encontrada.

**Lógica:** Usa `isBlank(s)` para nome, e-mail e rua; verifica password nulo ou length 0; verifica idArea nulo. Ordem das checagens define qual mensagem aparece primeiro.

#### Método privado: `isBlank(String s)`

- **Retorno:** `true` se `s == null || s.isBlank()`.

#### Método privado: `trimOrNull(String s)`

- **Retorno:** `null` se `s == null`; caso contrário `s.trim()`. Usado para campos opcionais (sobrenome, CEP, complemento, referência).

#### Método privado: `clearPassword(char[] password)`

- **Lógica:** Se `password != null`, preenche com `'0'` (Arrays.fill) para não deixar a senha na memória mais que o necessário.

#### Método privado: `persistUser(...)`

- **Assinatura:**  
  `private Optional<String> persistUser(String firstName, String lastName, String email, char[] password, Area area, String street, Integer number, String cep, String complement, String reference) throws Exception`
- **Retorno:** `Optional.empty()` em sucesso; `Optional.of(mensagem)` em falha.

**Ordem de persistência (obrigatória por FK):**

1. **Address:**  
   `buildAddress(area, street, number, cep, complement, reference)` monta o objeto Address.  
   `repoAddress.createAddressAndReturnId(address)` → retorna o ID do endereço. Se `null`, retorna "Error saving address."  
   `address.setInteger(idAddress)` (ajusta o ID no objeto).

2. **Person:**  
   Cria `Person(firstName.trim(), trimOrNull(lastName), email.trim(), address)`.  
   `repoPerson.createPersonAndReturnId(person)` → retorna ID. Se `null`, retorna "Error saving personal data (email may already be in use)."  
   `person.setId(idPerson)`.

3. **User:**  
   Cria `User(idPerson, ...)` (o construtor de User faz hash da senha).  
   `repoUser.createUser(user)`. Se `false`, retorna "Error creating user."

4. Retorna `Optional.empty()` se todos os passos derem certo.

#### Método privado: `buildAddress(...)`

- **Retorno:** Objeto `Address` preenchido com área, rua (trim), número, CEP/complemento/referência (trimOrNull). Não persiste; só monta o objeto para `persistUser` usar.

---

### 1.3 ControllerLogin

**Pacote:** `controller`  
**Responsabilidade:** Tentar **login** com e-mail e senha: buscar usuário por e-mail e validar senha com `EncryptionService.checkPassword`. Retorna o `User` logado ou `null`. Não desenha nada; a view decide o que fazer com sucesso ou falha.

#### Atributos (campos)

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `repoUser` | `RepositoryUser` | Busca usuário por e-mail (`findByEmailUser`). |

É `final` e injetado pelo construtor.

#### Construtor

```java
public ControllerLogin(RepositoryUser repoUser)
```

Apenas atribui o repositório.

#### Método: `login(String email, char[] password)`

- **Assinatura:** `public User login(String email, char[] password)`
- **Retorno:** `User` se credenciais válidas; `null` se inválidas ou em caso de exceção.

**Passo a passo:**

1. `FlowHandler.log("LOGIN_ATTEMPT")`.
2. Se `isEmptyCredentials(email, password)` (e-mail nulo/em branco ou senha nula/vazia): retorna `null` sem acessar o banco.
3. Try:
   - `User user = repoUser.findByEmailUser(email.trim())`.
   - `boolean ok = user != null && EncryptionService.checkPassword(password, user.getPasswordHash())`.
   - Se `ok`: log "LOGIN_OK"; senão: log "LOGIN_FAILED".
   - Retorna `ok ? user : null`.
4. Catch `SQLException`: imprime stack, log "LOGIN_FAILED" ("db"), retorna `null`.
5. Catch `Exception`: imprime stack, log "LOGIN_FAILED" com mensagem, retorna `null`.

**Uso na ViewLogin:** Se retorno for `null`, mostra "E-mail ou senha incorretos."; se for `User`, chama `onLoginSuccess.accept(this)`.

#### Método privado: `isEmptyCredentials(String email, char[] password)`

- **Retorno:** `true` se `email == null || email.isBlank()` ou `password == null || password.length == 0`.

---

## 2. Views

Todas as views são janelas Swing (`JFrame`), montam a tela em métodos (ex.: `configureFrame`, `buildMainPanel`, `buildTitleSection`) e usam **ViewTheme** para cores e fontes.

---

### 2.1 ViewTheme

**Pacote:** `view`  
**Responsabilidade:** Centralizar **cores**, **fontes** e **criação de componentes** com estilo do tema (paleta “confeitaria”: creme, dourado, marrom). Todas as outras views usam apenas os métodos estáticos do ViewTheme; não repetem configuração de cor/fonte.

**Conceito:** Classe utilitária; construtor privado (`private ViewTheme() {}`) para não instanciar.

#### Constantes – Cores

| Constante | Valor (hex) | Uso |
|-----------|-------------|-----|
| `BACKGROUND` | 0xFFF9F5 | Fundo principal das telas (creme). |
| `CARD_BG` | 0xFFFDFB | Fundo de cards/botões secundários. |
| `ACCENT` | 0xB8860B (dark goldenrod) | Botão primário (Cadastrar, Entrar). |
| `ACCENT_HOVER` | 0xCD9B1D | Borda do botão primário. |
| `TEXT` | 0x4A3728 | Texto principal. |
| `TEXT_MUTED` | 0x6B5344 | Subtítulos e texto secundário. |
| `BORDER` | 0xE8D5C4 | Bordas suaves (campos, seções). |
| `INPUT_BG` | Color.WHITE | Fundo dos campos de texto. |

#### Constantes – Fontes

| Constante | Família | Estilo | Tamanho | Uso |
|-----------|---------|--------|---------|-----|
| `FONT_FAMILY` | "Segoe UI" | — | — | Base para as demais. |
| `FONT_TITLE` | Segoe UI | BOLD | 22 | Títulos (ex.: "Sistema de Confeitaria"). |
| `FONT_SUBTITLE` | Segoe UI | PLAIN | 14 | Subtítulos. |
| `FONT_LABEL` | Segoe UI | PLAIN | 12 | Rótulos de campos. |
| `FONT_BUTTON` | Segoe UI | BOLD | 13 | Texto dos botões. |

#### Métodos estáticos – Painéis e rótulos

| Método | Parâmetros | Retorno | Descrição |
|--------|------------|---------|-----------|
| `createPanel(int top, int left, int bottom, int right)` | Margens (px) | `JPanel` | Painel com `BACKGROUND` e `EmptyBorder` nas margens. |
| `createTitleLabel(String text)` | Texto | `JLabel` | Fonte FONT_TITLE, cor TEXT. |
| `createSubtitleLabel(String text)` | Texto | `JLabel` | Fonte FONT_SUBTITLE, cor TEXT_MUTED. |
| `createFieldLabel(String text)` | Texto | `JLabel` | Fonte FONT_LABEL, cor TEXT. |

#### Métodos estáticos – Botões

| Método | Parâmetros | Retorno | Descrição |
|--------|------------|---------|-----------|
| `createPrimaryButton(String text)` | Texto do botão | `JButton` | Fundo ACCENT, texto branco, borda ACCENT_HOVER, cursor mão, focusPainted false. |
| `createSecondaryButton(String text)` | Texto do botão | `JButton` | Fundo CARD_BG, texto TEXT, borda BORDER, cursor mão. |

#### Métodos estáticos – Campos

| Método | Parâmetros | Retorno | Descrição |
|--------|------------|---------|-----------|
| `createTextField(int columns)` | Número de colunas | `JTextField` | Fonte FONT_LABEL, fundo INPUT_BG, borda BORDER, padding 6,8. |
| `createPasswordField(int columns)` | Número de colunas | `JPasswordField` | Mesmo estilo do text field. |

#### Método estático – Seção

| Método | Parâmetros | Retorno | Descrição |
|--------|------------|---------|-----------|
| `createSection(String title)` | Título da seção | `JPanel` | Layout BorderLayout(0,6); borda inferior BORDER 1px; rótulo com título em negrito 13f, cor ACCENT; usado para "Dados pessoais" e "Endereço". |

---

### 2.2 ViewHome

**Pacote:** `view`  
**Estende:** `JFrame`  
**Responsabilidade:** Tela inicial com título "Sistema de Confeitaria", subtítulo e dois botões: "Cadastrar" e "Entrar". Repassa os cliques ao **ControllerHome**. O controller é injetado **depois** da construção via `setController`, porque o `Main` precisa criar primeiro a ViewHome e o ControllerHome para então ligar um no outro.

#### Atributos

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `controller` | `ControllerHome` | Definido por `setController()`; usado nos listeners dos botões. Não é final para permitir injeção tardia. |

#### Construtor

```java
public ViewHome()
```

**Passos:**

1. `configureFrame()` — configura título, tamanho, fechamento, fundo, etc.
2. `setContentPane(buildMainPanel())` — define o conteúdo da janela.

#### Método: `configureFrame()`

- **Visibilidade:** private  
- **O que faz:**  
  - `setTitle("Sistema de Confeitaria")`  
  - `setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)` — fechar esta janela encerra o app.  
  - `setSize(420, 320)`  
  - `setLocation(80, 80)`  
  - `setResizable(false)`  
  - `getContentPane().setBackground(ViewTheme.BACKGROUND)`  
  - `setState(Frame.NORMAL)` — garante que não inicie minimizada (útil em VNC).

#### Método: `buildMainPanel()`

- **Retorno:** `JPanel`  
- **Layout:** BoxLayout(Y_AXIS). Margens 48,56,48,56 (ViewTheme.createPanel).  
- **Conteúdo (ordem):**  
  1. `buildTitleSection()`  
  2. `Box.createVerticalStrut(36)`  
  3. `buildButtonsSection()`

#### Método: `buildTitleSection()`

- **Retorno:** `Component` (painel da seção de título).  
- **Layout:** BoxLayout(Y_AXIS), alinhamento central (CENTER_ALIGNMENT).  
- **Conteúdo:**  
  - JLabel título: "Sistema de Confeitaria" (createTitleLabel).  
  - Strut 8px.  
  - JLabel subtítulo: "Cadastre-se ou entre para continuar" (createSubtitleLabel).  
  - Ambos com setAlignmentX(CENTER_ALIGNMENT).

#### Método: `buildButtonsSection()`

- **Retorno:** `Component` (painel dos botões).  
- **Conteúdo:**  
  - Botão "Cadastrar" (createPrimaryButton): maxSize 220x44, listener `e -> controller.openRegister()`.  
  - Strut 14px.  
  - Botão "Entrar" (createSecondaryButton): maxSize 220x44, listener `e -> controller.openLogin()`.  
  - Seção com setAlignmentX(CENTER_ALIGNMENT).  

**Importante:** Se `controller` for null (antes de setController), clicar nos botões causará NullPointerException. O Main deve chamar `setController` antes de exibir a janela.

#### Método: `setController(ControllerHome controller)`

- **Assinatura:** `public void setController(ControllerHome controller)`  
- **Uso:** Chamado pelo `Main` após criar o ControllerHome, para que os botões passem a chamar `openRegister()` e `openLogin()`.

---

### 2.3 ViewCadastro

**Pacote:** `view`  
**Estende:** `JFrame`  
**Responsabilidade:** Tela de cadastro com formulário (dados pessoais + endereço), combo de áreas, botões Voltar e Cadastrar. Coleta os dados, chama `ControllerCadastro.register(...)`; se retorno for String mostra JOptionPane e não fecha; se for null mostra "Cadastro realizado com sucesso!" e chama `dispose()`. Ao fechar (Voltar ou X), o WindowListener chama `onClose.run()` (voltar à home).

#### Atributos

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `controller` | `ControllerCadastro` | Recebido no construtor; usado em listAreas() e register(). |
| `onClose` | `Runnable` | Callback passado pelo ControllerHome; chamado ao fechar a janela (e no botão Voltar). |
| `fieldFirstName` | `JTextField` | Nome. |
| `fieldLastName` | `JTextField` | Sobrenome. |
| `fieldEmail` | `JTextField` | E-mail. |
| `fieldPassword` | `JPasswordField` | Senha. |
| `comboArea` | `JComboBox<Area>` | Área (região); itens são objetos Area, renderer mostra getName(). |
| `fieldStreet` | `JTextField` | Rua. |
| `fieldNumber` | `JTextField` | Número (string; convertido para Integer na submissão). |
| `fieldCep` | `JTextField` | CEP. |
| `fieldComplement` | `JTextField` | Complemento. |
| `fieldReference` | `JTextField` | Referência. |

Os campos de texto são atribuídos durante a construção (ex.: `fieldFirstName = ViewTheme.createTextField(20)` dentro de addFieldRow).

#### Construtor

```java
public ViewCadastro(ControllerCadastro controller, Runnable onClose)
```

**Passos:**

1. Guarda `controller` e `onClose`.
2. `configureFrame()`.
3. `setContentPane(buildMainPanel())`.

#### Método: `configureFrame()`

- Título: "Cadastrar - Sistema de Confeitaria".  
- `DISPOSE_ON_CLOSE` — fechar só esta janela; o app continua (a ViewHome está apenas invisível).  
- Tamanho 440x560, centralizada (`setLocationRelativeTo(null)`), não redimensionável.  
- Fundo BACKGROUND.  
- **WindowListener:** em `windowClosed` chama `onClose.run()` para o ControllerHome mostrar a ViewHome de novo.

#### Método: `buildMainPanel()`

- **Retorno:** `JScrollPane` (para rolar se o formulário for grande).  
- O viewport do scroll contém um JPanel com:  
  - buildTitleSection() — "Novo cadastro" e "Preencha os dados para se cadastrar".  
  - Strut 16.  
  - buildPersonalSection() — seção "Dados pessoais" com nome, sobrenome, e-mail, senha.  
  - Strut 8.  
  - buildAddressSection() — seção "Endereço" com área (combo), rua, número, CEP, complemento, referência.  
  - Strut 20.  
  - buildButtonsSection() — Voltar e Cadastrar.  
- Scroll: borda null, viewport com fundo BACKGROUND.

#### Método: `buildPersonalSection()`

- Usa `ViewTheme.createSection("Dados pessoais")`.  
- Adiciona linhas com `addFieldRow("Label", field)`: Nome *, Sobrenome, E-mail *, Senha *.  
- Campos criados com createTextField(20) ou createPasswordField(20) e atribuídos aos campos da classe.

#### Método: `buildAddressSection()`

- Usa `ViewTheme.createSection("Endereço")`.  
- Rótulo "Área (região) *", depois `buildAreaCombo()`.  
- Em seguida: Rua *, Número, CEP, Complemento, Referência (addFieldRow).  
- fieldNumber é texto; a conversão para Integer é feita em onRegisterClick (parseIntOrNull).

#### Método: `buildAreaCombo()`

- Chama `controller.listAreas()`. Em exceção usa lista vazia.  
- Cria `JComboBox<Area>` com os itens; aplica FONT_LABEL, INPUT_BG, borda.  
- **ListCellRenderer:** para cada item (Area) exibe `a.getName()`.  
- Atribui o combo a `comboArea` e retorna.

#### Método: `addFieldRow(String labelText, JComponent field)`

- **Retorno:** `JPanel` com BorderLayout(0,4): rótulo em NORTH, field em CENTER.  
- Usado para padronizar todas as linhas de label + campo.

#### Método: `buildButtonsSection()`

- FlowLayout centralizado, espaçamento 12.  
- Botão "Voltar" (secundário): listener chama `onBackClick()`.  
- Botão "Cadastrar" (primário): listener chama `onRegisterClick()`.

#### Método: `onRegisterClick()`

**Passo a passo:**

1. Obtém área selecionada: se combo tem itens, `(Area) comboArea.getSelectedItem()`; senão null. `idArea = area != null ? area.getId() : null`.
2. Número: `fieldNumber.getText()` → se vazio ou em branco, number = null; senão `parseIntOrNull(numStr.trim())` (retorna null se não for número).
3. Chama `controller.register(fieldFirstName.getText(), fieldLastName.getText(), fieldEmail.getText(), fieldPassword.getPassword(), idArea, fieldStreet.getText(), number, fieldCep.getText(), fieldComplement.getText(), fieldReference.getText())`.
4. Se retorno `error != null`: `JOptionPane.showMessageDialog(..., error, "Cadastro", WARNING_MESSAGE)` e return (não fecha).
5. Se retorno `null`: `JOptionPane.showMessageDialog(..., "Cadastro realizado com sucesso!", ..., INFORMATION_MESSAGE)` e `dispose()`. O WindowListener então chama `onClose.run()`.

#### Método: `parseIntOrNull(String s)`

- **Estático.** Retorna `Integer.parseInt(s)` ou null se s nulo/vazio ou NumberFormatException.

#### Método: `onBackClick()`

- Chama `dispose()` e `onClose.run()`. Voltar tem o mesmo efeito de fechar pela janela: volta à home.

---

### 2.4 ViewLogin

**Pacote:** `view`  
**Estende:** `JFrame`  
**Responsabilidade:** Tela de login (e-mail e senha, botões Voltar e Entrar). Chama `ControllerLogin.login(email, password)`; se retornar User chama `onLoginSuccess.accept(this)` (ControllerHome fecha a login e abre o dashboard); senão mostra "E-mail ou senha incorretos." e não fecha.

#### Atributos

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `controller` | `ControllerLogin` | Recebido no construtor. |
| `onBack` | `Runnable` | Callback ao fechar (Voltar ou X) — mostrar home. |
| `onLoginSuccess` | `Consumer<ViewLogin>` | Callback quando login OK; recebe a própria ViewLogin para o ControllerHome dar dispose nela. |
| `fieldEmail` | `JTextField` | E-mail. |
| `fieldPassword` | `JPasswordField` | Senha. |

#### Construtor

```java
public ViewLogin(ControllerLogin controller, Runnable onBack, Consumer<ViewLogin> onLoginSuccess)
```

Guarda os três parâmetros, chama `configureFrame()` e `setContentPane(buildMainPanel())`.

#### Método: `configureFrame()`

- Título: "Entrar - Sistema de Confeitaria".  
- DISPOSE_ON_CLOSE.  
- Tamanho 400x300, centralizada, não redimensionável, fundo BACKGROUND.  
- **WindowListener:** em `windowClosed` chama `onBack.run()`.

#### Método: `buildMainPanel()`

- Painel com margens 32,44,32,44, BoxLayout Y_AXIS.  
- Conteúdo: buildTitleSection() ("Entrar"), strut 24, buildFieldsSection(), strut 24, buildButtonsSection().

#### Método: `buildTitleSection()`

- Um JLabel "Entrar" (createTitleLabel), CENTER_ALIGNMENT.

#### Método: `buildFieldsSection()`

- Rótulo "E-mail", strut 4, fieldEmail (createTextField(22)).  
- Strut 12.  
- Rótulo "Senha", strut 4, fieldPassword (createPasswordField(22)).  
- fieldEmail e fieldPassword são atribuídos aqui aos campos da classe.

#### Método: `buildButtonsSection()`

- FlowLayout centralizado.  
- "Voltar" → `onBackClick()`.  
- "Entrar" → `onLoginClick()`.

#### Método: `onLoginClick()`

1. Lê `fieldEmail.getText()` e `fieldPassword.getPassword()`.  
2. `User user = controller.login(email, password)`.  
3. Se `user == null`: JOptionPane "E-mail ou senha incorretos.", return.  
4. Se user não null: `onLoginSuccess.accept(this)`. O ControllerHome em `onLoginSuccess(ViewLogin)` faz dispose da ViewLogin e abre a ViewDashboard.

#### Método: `onBackClick()`

- `dispose()` e `onBack.run()` — volta à home.

---

### 2.5 ViewDashboard

**Pacote:** `view`  
**Estende:** `JFrame`  
**Responsabilidade:** Tela interna (área logada). Placeholder: título "Área interna" e mensagem "Você entrou no sistema. Em breve: pedidos, produtos e mais." Não recebe controller; é criada e exibida diretamente pelo ControllerHome após login bem-sucedido. Fechar esta janela encerra o app (EXIT_ON_CLOSE).

#### Atributos

Nenhum campo além do que o JFrame já possui. Não guarda controller nem callbacks.

#### Construtor

```java
public ViewDashboard()
```

Chama `configureFrame()` e `setContentPane(buildMainPanel())`.

#### Método: `configureFrame()`

- Título: "Sistema de Confeitaria - Área interna".  
- EXIT_ON_CLOSE.  
- Tamanho 520x360, centralizada.  
- Fundo BACKGROUND.  
- Não define setResizable (padrão true).

#### Método: `buildMainPanel()`

- Painel com margens 48, BoxLayout Y_AXIS.  
- buildWelcomeSection() — rótulo "Área interna" (createTitleLabel), CENTER_ALIGNMENT.  
- Strut 24.  
- buildMessageSection() — rótulo com o texto longo (createSubtitleLabel), CENTER_ALIGNMENT.

#### Método: `buildWelcomeSection()`

- Retorna um JLabel "Área interna" com estilo de título, alinhado ao centro.

#### Método: `buildMessageSection()`

- Retorna um JLabel com "Você entrou no sistema. Em breve: pedidos, produtos e mais.", subtítulo, alinhado ao centro.

---

## Resumo das relações

| Quem | Usa / Chama |
|------|------------------|
| **Main** | Cria ViewHome, repositórios, ControllerCadastro, ControllerLogin, ControllerHome; chama viewHome.setController(controllerHome) e viewHome.setVisible(true). |
| **ControllerHome** | viewHome (esconder/mostrar), ControllerCadastro e ControllerLogin (para instanciar ViewCadastro e ViewLogin), ViewCadastro/ViewLogin (callbacks backToHome e onLoginSuccess). |
| **ControllerCadastro** | RepositoryPerson, RepositoryUser, RepositoryAddress, RepositoryArea (todos para validar e persistir). |
| **ControllerLogin** | RepositoryUser, EncryptionService.checkPassword. |
| **ViewHome** | ControllerHome (openRegister, openLogin), ViewTheme. |
| **ViewCadastro** | ControllerCadastro (listAreas, register), Runnable onClose, ViewTheme. |
| **ViewLogin** | ControllerLogin (login), Runnable onBack, Consumer onLoginSuccess, ViewTheme. |
| **ViewDashboard** | Apenas ViewTheme; aberta pelo ControllerHome. |
| **ViewTheme** | Nenhuma outra view/controller; só Swing e AWT. |

Este documento cobre cada método, cada atributo e o fluxo entre views e controllers com o máximo de detalhe possível a partir do código atual.
