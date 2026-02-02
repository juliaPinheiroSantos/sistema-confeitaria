# Guia: Reset e operações do banco no Docker

Este documento descreve como resetar o banco de dados, listar tabelas e executar comandos úteis quando o projeto roda com **Docker Compose**. Use-o quando precisar corrigir a estrutura das tabelas, zerar dados ou inspecionar o PostgreSQL.

---

## Informações do ambiente

| Item | Valor |
|------|--------|
| **Container do Postgres** | `confeitaria-postgres` |
| **Container da aplicação** | `confeitaria-app` |
| **Banco de dados** | `confeitaria` |
| **Usuário** | `admin` |
| **Senha** | `12345` |
| **Porta no host** | `5434` (mapeada para 5432 no container) |
| **Volume de dados** | `postgres_data` (nome completo costuma ser `<pasta_do_projeto>_postgres_data`, ex.: `sistemaconfeitaria_postgres_data`) |

Execute os comandos na pasta onde está o `docker-compose.yml` (geralmente `sistemaConfeitaria/`). No **PowerShell** use os blocos marcados como `powershell`; no **Bash/WSL** pode usar os mesmos ou os marcados como `bash` quando houver diferença.

---

## Comandos explicados

### Listar tabelas do banco

Mostra todas as tabelas do schema `public`.

**Comando:**

```powershell
docker exec -t confeitaria-postgres psql -U admin -d confeitaria -c "\dt"
```

**Explicação:**

- `docker exec` — executa um comando dentro de um container em execução.
- `-t` — aloca um pseudo-TTY (útil para saída formatada).
- `confeitaria-postgres` — nome do container do Postgres.
- `psql` — cliente de linha de comando do PostgreSQL (**não** `psl`).
- `-U admin` — usuário do banco.
- `-d confeitaria` — nome do banco.
- `-c "\dt"` — comando a executar e sair. No psql, `\dt` lista tabelas (meta-comando com **barra invertida** `\`, não `/`).

Se no PowerShell `\dt` der problema, use aspas simples:

```powershell
docker exec -t confeitaria-postgres psql -U admin -d confeitaria -c '\dt'
```

---

## Opção 1: Dropar apenas a tabela `user`

Use quando só a tabela `user` estiver com estrutura errada e você quiser que o `CreateTables` a recrie na próxima subida da app.

**Por que entre aspas?** No PostgreSQL, `user` é palavra reservada. O nome da tabela no código é `"user"` (entre aspas duplas). No SQL também precisa ser `"user"`.

**Comando (uma linha):**

```powershell
docker exec -it confeitaria-postgres psql -U admin -d confeitaria -c "DROP TABLE IF EXISTS \"user\" CASCADE;"
```

**Explicação:**

- `-it` — modo interativo + TTY (para comandos que pedem confirmação; aqui não é obrigatório, mas não atrapalha).
- `DROP TABLE IF EXISTS "user"` — remove a tabela `user` se existir.
- `CASCADE` — remove também objetos que dependem da tabela (ex.: FKs de outras tabelas).

**Depois:** subir/recriar a aplicação para o `CreateTables` rodar e criar a tabela de novo:

```powershell
docker compose up -d --build app
```

- `up -d` — sobe os serviços em segundo plano.
- `--build app` — reconstrói apenas a imagem do serviço `app` antes de subir.

---

## Opção 2: Recriar todo o banco (zerar todos os dados)

Use quando quiser um banco totalmente limpo, com todas as tabelas recriadas pelo `CreateTables`.

### Passo 2.1: Parar os containers

```powershell
docker compose down
```

**Explicação:** Para e remove os containers (e redes) definidos no `docker-compose.yml`. Os **volumes** continuam existindo; os dados do Postgres permanecem no volume.

### Passo 2.2: Remover o volume do Postgres (apaga todos os dados)

**Forma recomendada (remove volumes declarados no compose):**

```powershell
docker compose down -v
```

**Explicação:** O `-v` remove os volumes nomeados usados pelos serviços. Assim, na próxima subida, o Postgres criará um banco vazio.

**Forma alternativa (remover um volume específico):**

Se você só parou com `docker compose down` (sem `-v`) e quiser apagar o volume à mão:

```powershell
docker volume rm sistemaconfeitaria_postgres_data
```

O nome exato do volume pode variar (ex.: `sistemaconfeitaria_postgres_data` ou `sistemaConfeitaria_postgres_data`). Para listar os volumes:

```powershell
docker volume ls
```

Procure o que contém `postgres_data` no nome.

### Passo 2.3: Subir de novo (banco limpo + app)

```powershell
docker compose up -d --build
```

**Explicação:**

- `up -d` — sobe todos os serviços em segundo plano.
- `--build` — reconstrói as imagens antes de subir (importante após mudar código).

O Postgres sobe com banco vazio; a aplicação sobe e o `CreateTables` cria todas as tabelas na ordem correta.

---

## Opção 3: Abrir o psql e rodar SQL à mão (sem apagar volume)

Use quando quiser executar vários comandos SQL ou meta-comandos do psql (como `\dt`, `\d tabela`) sem remover o volume.

**Abrir o psql no container:**

```powershell
docker exec -it confeitaria-postgres psql -U admin -d confeitaria
```

**Explicação:**

- `-it` — mantém a sessão interativa (você digita no prompt do psql).
- Sem `-c` — o psql abre e espera seus comandos.

**Comandos úteis dentro do psql:**

| Comando | Descrição |
|--------|------------|
| `\dt` | Lista tabelas do schema atual |
| `\d nome_tabela` | Descreve colunas e constraints da tabela |
| `\q` | Sair do psql |

**Exemplo — dropar só a tabela `user` e sair:**

```sql
DROP TABLE IF EXISTS "user" CASCADE;
\q
```

**Depois:** reiniciar a app para o `CreateTables` rodar de novo (se a tabela foi dropada):

```powershell
docker compose restart app
```

---

## Rebuild e subida da aplicação

Sempre que alterar código Java (por exemplo `CreateTables.java` ou repositórios), é preciso **reconstruir** a imagem da app e subir de novo:

```powershell
docker compose up -d --build app
```

Ou, para reconstruir sem usar cache da build anterior:

```powershell
docker compose build --no-cache app
docker compose up -d app
```

**Ver logs da aplicação** (para checar se as tabelas foram criadas ou se houve erro):

```powershell
docker compose logs app
```

**Ver logs em tempo real:**

```powershell
docker compose logs -f app
```

---

## Resumo rápido de comandos

| Objetivo | Comando |
|----------|---------|
| Listar tabelas | `docker exec -t confeitaria-postgres psql -U admin -d confeitaria -c '\dt'` |
| Dropar só a tabela `user` | `docker exec -it confeitaria-postgres psql -U admin -d confeitaria -c "DROP TABLE IF EXISTS \"user\" CASCADE;"` |
| Zerar tudo e recriar banco | `docker compose down -v` e depois `docker compose up -d --build` |
| Abrir psql interativo | `docker exec -it confeitaria-postgres psql -U admin -d confeitaria` |
| Rebuild da app após mudar código | `docker compose up -d --build app` |
| Ver logs da app | `docker compose logs app` |

---

## Sobre o `.gitignore`

O repositório já ignora certos arquivos para não versionar builds, IDE e arquivos sensíveis. Entradas comuns para um projeto Java + Docker:

- **`/bin/`** — saída de compilação (ex.: Eclipse).
- **`.metadata/`**, **`.settings/`**, **`.classpath`**, **`.project`** — configuração do Eclipse.
- **`.vscode/`**, **`.idea/`** — configuração do VS Code e IntelliJ (podem conter caminhos locais).
- **`.gitignore`** — às vezes ignorado para evitar conflitos; depende da convenção do projeto.
- **`.md`** — no `.gitignore` atual, **todos os arquivos `.md`** são ignorados, incluindo este `docker-db-reset.md`.

**Se quiser versionar este guia** (`docker-db-reset.md`) no Git, é preciso permitir esse arquivo mesmo com `.md` no `.gitignore`. No `.gitignore`, adicione uma exceção:

```gitignore
# Permite versionar o guia de reset do banco
!docker-db-reset.md
```

A linha `!docker-db-reset.md` faz o Git **não** ignorar esse arquivo, mesmo que a regra `.md` exista acima. Assim você mantém o guia no repositório e o resto dos `.md` continua ignorado, se for o desejado.
