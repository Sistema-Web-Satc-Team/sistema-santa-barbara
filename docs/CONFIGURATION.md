# Configuração da aplicação

## Perfis de execução

O Sistema Santa Bárbara utiliza perfis Spring para separar as configurações de desenvolvimento, testes e produção.

| Perfil | Finalidade | Banco |
|---|---|---|
| `dev` | Desenvolvimento local | PostgreSQL local |
| `test` | Testes automatizados | PostgreSQL isolado |
| `prod` | Ambiente de produção | PostgreSQL configurado externamente |

O perfil padrão é `dev`.

Para selecionar outro perfil, configure a variável `SPRING_PROFILES_ACTIVE`.

## Variáveis de ambiente

### Banco de dados

| Variável | Descrição |
|---|---|
| `DATABASE_URL` | URL JDBC do PostgreSQL |
| `DATABASE_USERNAME` | Usuário do banco |
| `DATABASE_PASSWORD` | Senha do banco |
| `TEST_DATABASE_URL` | URL JDBC do banco de testes |
| `TEST_DATABASE_USERNAME` | Usuário do banco de testes |
| `TEST_DATABASE_PASSWORD` | Senha do banco de testes |

### Autenticação JWT

| Variável | Descrição |
|---|---|
| `JWT_PUBLIC_PEM_KEY_PATH` | Localização da chave pública |
| `JWT_PRIVATE_PEM_KEY_PATH` | Localização da chave privada |
| `JWT_EXPIRATION_SECONDS` | Tempo de validade do token, em segundos |

As chaves utilizadas em produção devem ser armazenadas fora do repositório e protegidas por permissões adequadas.

### CORS

| Variável | Descrição |
|---|---|
| `CORS_ALLOWED_ORIGINS` | Os sites que podem fazer requisição para a API |

### Frontend

| Variável | Descrição | Localização / Path |
|---|---|---|
| `FRONTEND_URL` | Endereço base do frontend, sujeito à confirmação de uso no código | `santa-barbara-backend/bandasantabarbara` |
| `FRONTEND_INVITATION_URL` | URL utilizada nos convites enviados aos membros | `santa-barbara-backend/bandasantabarbara` |
| `VITE_API_BASE_URL` | URL pública base do backend, utilizada pelo Axios | `santa-barbara-frontend` |

> As variáveis prefixadas com VITE_ são disponibilizadas ao código executado no navegador. Seus valores são incorporados durante a compilação do frontend. Portanto, não devem conter senhas, tokens privados ou outros segredos.

A URL de convite deve apontar para uma rota válida da aplicação web.

### Serviço de e-mail

| Variável | Descrição |
|---|---|
| `MAIL_HOST` | Servidor SMTP |
| `MAIL_PORT` | Porta SMTP |
| `MAIL_USERNAME` | Usuário de autenticação |
| `MAIL_PASSWORD` | Senha ou credencial SMTP |

Em desenvolvimento e testes, recomenda-se utilizar um servidor SMTP de testes para evitar o envio de mensagens reais.

Em produção, configure um provedor SMTP autorizado.

### Armazenamento

| Variável | Descrição |
|---|---|
| `STORAGE_LOCAL_ROOT` | Diretório utilizado para armazenar arquivos enviados |

O diretório de produção precisa possuir armazenamento persistente, permissões adequadas e uma estratégia de backup.

## Banco de dados e migrações

O projeto utiliza Flyway para gerenciar a evolução do esquema do banco de dados.

As migrações encontram-se em:

`src/main/resources/db/migration`

A propriedade `spring.jpa.hibernate.ddl-auto=validate` verifica a compatibilidade entre as entidades JPA e o esquema existente.

As alterações estruturais devem ser realizadas por migrações versionadas.

## Segurança

Antes de publicar a aplicação:

- Configure credenciais próprias para produção.
- Utilize HTTPS.
- Proteja as chaves privadas JWT.
- Verifique as permissões dos endpoints.
- Não exponha informações sensíveis nos logs.
- Configure backups do banco e dos arquivos.
- Verifique as políticas de CORS.
- Valide o comportamento de autenticação e autorização.
- Não utilize o perfil de desenvolvimento em produção.

## Observações sobre a implantação

O processo de implantação depende da infraestrutura escolhida.

A compilação do frontend, a publicação dos arquivos estáticos, a execução do backend e a configuração do PostgreSQL são responsabilidades distintas.

A disponibilização da aplicação deve ser acompanhada de testes de integração e verificação dos serviços após a implantação.
