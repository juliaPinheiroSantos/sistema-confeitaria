## Tela de Produtos (CRUD) — um capitulo contado como livro

Imagine que a aplicacao e uma pequena confeitaria digital. Ao terminar o login, a porta da loja se abre e o usuario entra na sala principal dos produtos. E aqui que a historia comeca: a tela `ViewProducts` aparece como o balcao de trabalho, pronta para criar, editar e organizar tudo o que sera vendido.

### Capitulo 1 — A entrada na sala

Depois do login, o fluxo conduz o usuario para a tela de produtos. O login se encerra e, no lugar, surge a area onde os produtos moram. Esse caminho acontece no `ControllerHome`, que recebe a confirmacao do login e abre a tela `ViewProducts`.

Arquivos que contam essa passagem:
- `src/controller/ControllerHome.java`
- `src/view/ViewLogin.java`
- `src/view/ViewProducts.java`

### Capitulo 2 — O cenario da tela

A tela e dividida como um livro aberto: do lado esquerdo, o formulario; do lado direito, a tabela. No topo, um cabecalho apresenta o titulo “Produtos” e o total de itens comprados, como um marcador da jornada.

Tudo isso esta concentrado em:
- `src/view/ViewProducts.java`

### Capitulo 3 — O formulario, onde o produto ganha vida

O formulario e o lugar onde o produto nasce. Ele possui nome, preco base, sabor, tamanho e descricao. Enquanto o usuario escolhe o sabor ou o tamanho, o sistema atualiza o preco final automaticamente. E como se o valor de uma receita fosse calculado enquanto ela e montada.

O preco final segue a regra: base + tamanho + nivel do sabor.

### Capitulo 4 — A tabela, o registro da loja

A tabela guarda todos os produtos criados. Cada linha e um item cadastrado, com colunas que exibem nome, sabor, tamanho, preco e descricao. Ao selecionar uma linha, o formulario se preenche e permite editar.

### Capitulo 5 — Os botoes, ferramentas do oficio

Os botoes representam as acoes principais:
- `Novo` limpa o formulario
- `Salvar` cria um produto
- `Atualizar` edita o produto selecionado
- `Excluir` remove um produto
- `Recarregar` traz a lista do banco
- `Seed padrao` cria sabores e tamanhos padrao quando o banco esta vazio

Se o banco estiver sem dados, a tela exibe sabores e tamanhos padrao apenas para visualizacao. Nesse caso o CRUD fica bloqueado, e o usuario deve clicar em “Seed padrao” para criar os dados reais.

### Capitulo 6 — As regras e as mensagens

O sistema verifica se o nome foi preenchido, se sabor e tamanho foram escolhidos e se o preco base e valido. Se algo estiver faltando, mensagens aparecem guiando o usuario. Tambem ha aviso quando o banco esta vazio ou quando o seed termina com sucesso.

### Capitulo 7 — Os personagens do bastidor

O controller `ControllerProduct` faz a ponte entre tela e banco. Ele lista produtos, cria, atualiza, remove e calcula o total comprado. Alem disso, ele executa o seed manual quando solicitado.

Repositorios que sustentam esse trabalho:
- `RepositoryProduct`
- `RepositoryFlavor`
- `RepositorySize`
- `RepositoryOrderItems`
- `RepositoryFlavorLevel`

### Capitulo 7.1 — Explicando o codigo, passo a passo

Para quem quer ver como o codigo funciona por dentro, aqui vai uma leitura guiada:

1. **Construtor da tela**  
   Quando `ViewProducts` e criada, ela configura a janela, monta o layout, carrega os combos e a tabela.  
   Isso garante que a tela ja apareca preenchida com dados do banco.

2. **Carregamento de sabores e tamanhos**  
   O metodo `loadCombos()` chama o controller para buscar sabores e tamanhos.  
   Se o banco estiver vazio, o fallback visual usa os enums como dados padrao e bloqueia o CRUD.

3. **Seed manual**  
   O botao `Seed padrao` chama `controller.seedDefaults()`.  
   Esse metodo cria niveis, sabores e tamanhos no banco, evitando duplicidade.

4. **Tabela de produtos**  
   `refreshTable()` busca tudo em `RepositoryProduct` e monta as linhas da tabela.  
   Ao selecionar uma linha, `onTableSelection()` preenche o formulario com o produto escolhido.

5. **Salvar, atualizar e excluir**  
   - `Salvar` cria um `Product` novo e chama `createProduct()` no controller.  
   - `Atualizar` pega o produto selecionado na tabela e chama `updateProduct()`.  
   - `Excluir` remove o produto selecionado usando `deleteProduct()`.

6. **Preco final automatico**  
   Sempre que o usuario muda o preco base, sabor ou tamanho, `updateFinalPrice()` recalcula:  
   `base + preco do tamanho + preco do nivel do sabor`.

### Capitulo 8 — O banco como registro definitivo

No banco, `product` referencia `flavor` e `size`, e o total comprado vem de `order_items`. O seed garante que os sabores e tamanhos existam, evitando duplicidade, e o insert de `flavor` preenche o campo `price` usando o valor do nivel.

### Capitulo 9 — O ritual de teste

Para testar a historia completa:
1. Crie dados minimos (`flavor_level`, `flavor`, `size`) ou rode o `Seed padrao`
2. Faca login
3. Cadastre um produto
4. Veja a tabela preencher
5. Edite e exclua
6. Confira o total comprado

E assim termina o capitulo da tela de produtos: um lugar onde cada detalhe do produto ganha forma, e onde a confeitaria digital organiza sua vitrine com clareza.

