# Sistema Santa Barbara

Sistema de escola de música.

## Requisitos do projeto e dependências

- Docker & Docker Compose
- PostgreSQL
- Java 25
- Node.js 22v
- Java Spring Boot & Maven
- Flyway

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
