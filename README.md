# Sistema Santa Barbara

Sistema de escola de música.

## Requisitos do projeto

- Docker & Docker Compose
- Java 25
- Node.js 22v

# CLI de Desenvolvimento (`dev-cli`)

O orquestrador do ambiente automatiza a subida do banco de dados no Docker (PostgreSQL), compilação e execução do backend (Spring Boot) e servidor de desenvolvimento do frontend (Vite/React).

> **Nota:** No Windows, você pode executar o comando diretamente como `.\dev-cli` (ou `dev-cli`). No Linux e macOS, utilize o executável com o sufixo do seu sistema operacional (`./dev-cli-linux` ou `./dev-cli-macos`).

---

## Usando dev-cli no Windows

> **Dica:** Lembre-se de conceder permissão de execução caso necessário.

Para instalar docker (provavelmente você teria que instalar manualmente):

```bash
.\dev-cli install
```

Para Inicar:

```bash
.\dev-cli init
```

Para Começar:

```bash
.\dev-cli run
```

Para Parar:

```bash
.\dev-cli stop
```

> **Dica**: você pode usar `ctrl + c` no terminal que faz o mesmo efeito.

Destruir volumes Docker e limpar `node_modules`:

```bash
.\dev-cli destroy
```

## Usando dev-cli no Linux

> **Dica:** Antes de executar pela primeira vez, garanta a permissão de execução com `chmod +x dev-cli-linux`.

Para instalar docker (provavelmente você teria que instalar manualmente):

```bash
./dev-cli-linux install
```

Para Inicar:

```bash
./dev-cli-linux init
```

Para Começar:

```bash
./dev-cli-linux run
```

Para Parar:

```bash
./dev-cli-linux stop
```

> **Dica**: você pode usar `ctrl + c` no terminal que faz o mesmo efeito.

Destruir volumes Docker e limpar `node_modules`:

```bash
./dev-cli-linux destroy
```

## Usando dev-cli no Macos

> **Dica**: Antes de executar pela primeira vez, garanta a permissão de execução com `chmod +x dev-cli-macos`.

Para instalar docker (provavelmente você teria que instalar manualmente):

```bash
./dev-cli-macos install
```

Para Inicar:

```bash
./dev-cli-macos init
```

Para Começar:

```bash
./dev-cli-macos run
```

Para Parar:

```bash
./dev-cli-macos stop
```

> **Dica**: você pode usar `ctrl + c` no terminal que faz o mesmo efeito.

Destruir volumes Docker e limpar `node_modules`:

```bash
./dev-cli-macos destroy
```

# Docker Usage

Para iniciar:

```bash
docker compose up -d
```

Para limpar tudo:

```bash
docker compose down -v
```

Para ver todas as tabelas:

```bash
docker exec -it sb-postgres psql -U postgres -d postgres -c "\dt"
```

Para parar a execução:

```bash
docker compose stop
```

Para voltar com a execução:

```bash
docker compose start
```

Para listar os serviços em execução:

```bash
docker compose ls
```

Para listar todos os serviços:

```bash
docker compose ls -a
```
