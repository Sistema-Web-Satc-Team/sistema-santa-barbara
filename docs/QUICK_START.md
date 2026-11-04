# Quick Start

Este guia apresenta os requisitos e procedimentos necessários para configurar e executar o Sistema Santa Bárbara em um ambiente local de desenvolvimento.

## 1. Pré-requisitos

Antes de iniciar, instale:

| Tecnologia | Versão |
|---|---|
| Java JDK | 25 |
| Node.js | 22 |
| Docker Engine ou Docker Desktop | Compatível com Docker Compose |
| Git | Versão recente |

O backend utiliza o Maven Wrapper, dispensando a instalação global do Maven.

## 2. Estrutura do projeto

O repositório contém:

- `santa-barbara-backend/bandasantabarbara`: API Spring Boot.
- `santa-barbara-frontend`: aplicação React.
- `docs`: documentação do projeto.
- `docker-compose.yml`: infraestrutura local.
- `README.md`: apresentação geral.

Os comandos abaixo partem da raiz do repositório, salvo indicação contrária.

## 3. Configurar o ambiente

Crie o arquivo `.env` a partir do exemplo:

```bash
cd santa-barbara-backend/bandasantabarbara
cp .env.example .env
```

Configure as variáveis necessárias para o ambiente escolhido.

**Atenção:** o Spring Boot não carrega automaticamente um arquivo `.env` como variáveis de ambiente. No perfil de desenvolvimento, o arquivo será importado como propriedades por meio de `spring.config.import`.

Se utilizar o Docker Compose, confirme também como ele utiliza essas variáveis.

## 4. Gerar chaves JWT

A aplicação utiliza chaves RSA para assinatura e validação dos tokens JWT.

Acesse o diretório de recursos:

```bash
cd santa-barbara-backend/bandasantabarbara
mkdir -p src/main/resources/certs
```

Gere uma chave privada RSA:

```bash
openssl genpkey -algorithm RSA \
  -out src/main/resources/certs/private.pem \
  -pkeyopt rsa_keygen_bits:3072
```

Gere a chave pública correspondente:

```bash
openssl rsa \
  -pubout \
  -in src/main/resources/certs/private.pem \
  -out src/main/resources/certs/public.pem
```

Essas chaves são destinadas ao desenvolvimento local.

Não versione a chave privada nem utilize chaves de desenvolvimento em produção.

## 5. Iniciar a infraestrutura

Retorne à raiz do repositório:

```bash
cd ../..
```

Inicie os serviços definidos no Docker Compose:

```bash
docker compose up -d
```

Verifique o estado dos contêineres:

```bash
docker compose ps
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para interromper e remover os contêineres:

```bash
docker compose down
```

O comando `down` não remove os volumes nomeados por padrão.

**Importante:** confira se os serviços definidos no Compose disponibilizam o PostgreSQL nas portas configuradas para desenvolvimento e testes.

## 6. Executar o backend

Acesse:

```bash
cd santa-barbara-backend/bandasantabarbara
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

Por padrão, será utilizado o perfil `dev`.

Para executar explicitamente com esse perfil:

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev
```

Com o backend iniciado, a API utiliza o prefixo:

```text
http://localhost:8080/api/v1
```

A porta acima considera a configuração padrão do Spring Boot.

## 7. Executar o frontend

Em outro terminal, acesse:

```bash
cd santa-barbara-frontend
```

Instale as dependências:

```bash
npm install
```

Criar configuração local:

```bash
cp .env.example .env.local
```

> Para produção basta adicionar em `.env.local` a URL da api backend.

Inicie o servidor de desenvolvimento:

```bash
npm run dev
```

O endereço do frontend será exibido no terminal.

Confira as variáveis de integração com a API antes de iniciar.

## 8. Compilar para produção

Para gerar a versão otimizada do frontend:

```bash
npm run build
```

Para visualizar a compilação localmente:

```bash
npm run preview
```

Caso o projeto possua um script de implantação configurado:

```bash
npm run deploy
```

O comando `deploy` deve ser utilizado somente após verificar seu comportamento no `package.json` e configurar o provedor de hospedagem.

## 9. Executar testes

Para executar os testes do backend:

```bash
cd santa-barbara-backend/bandasantabarbara
./mvnw test -Dspring.profiles.active=test
```

Os testes de integração que utilizam PostgreSQL exigem que o banco de testes esteja disponível.

## 10. Problemas comuns

### Porta 8080 ocupada

No Linux, identifique o processo:

```bash
sudo ss -lptn 'sport = :8080'
```

Finalize o processo somente depois de confirmar que ele pode ser interrompido.

### Falha na conexão com PostgreSQL

Verifique:

- Se o contêiner está ativo.
- Se a porta está correta.
- Se o banco existe.
- Se as credenciais correspondem à configuração do ambiente.

### Erro ao carregar chaves JWT

Verifique se os arquivos PEM existem, se os caminhos estão corretos e se a aplicação possui permissão para lê-los.

### Erro nas migrações

Consulte os logs do Flyway e verifique o estado do banco.

Não altere migrações já aplicadas em ambientes compartilhados sem planejar uma estratégia de migração compatível.

## 11. Próximos passos

Depois de iniciar o sistema, consulte a documentação de configuração e os fluxos de teste para compreender o funcionamento da aplicação.
