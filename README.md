# Sistema Santa Bárbara

**Versão:** 1.0.0  
**Data da Release:** Novembro de 2026  
**Status:** Em estabilização para a primeira entrega

## Sobre o projeto

O **Sistema Santa Bárbara** é uma aplicação web desenvolvida para auxiliar na gestão administrativa e musical da Banda Santa Bárbara, uma organização sem fins lucrativos.

O projeto surgiu durante a disciplina **Projeto Integrador: Sistema Web**, com o objetivo de identificar problemas reais de uma organização e desenvolver uma solução tecnológica aplicável ao seu cotidiano.

Durante o levantamento de requisitos, realizado por meio de entrevistas, reuniões e validações com representantes da organização, foram identificadas dificuldades relacionadas ao cadastro de membros, à organização de informações e à distribuição de partituras musicais.

Anteriormente, parte dessas atividades era realizada por registros físicos e pela comunicação via WhatsApp, dificultando a centralização, a consulta e a manutenção das informações.

A solução proposta consiste em uma plataforma web que centraliza o gerenciamento de membros e do acervo musical, oferecendo diferentes funcionalidades conforme o perfil de acesso de cada usuário.

## Objetivos

O objetivo principal é digitalizar e centralizar processos administrativos e musicais da organização.

Entre os objetivos específicos estão:

- Centralizar o cadastro de membros e suas informações.
- Organizar os vínculos dos participantes com a instituição.
- Permitir o envio de convites para acesso à plataforma.
- Disponibilizar um acervo digital de músicas e partituras.
- Organizar partituras conforme os instrumentos musicais.
- Controlar o acesso aos recursos conforme as permissões dos usuários.
- Reduzir a dependência de registros físicos e do compartilhamento de documentos pelo WhatsApp.

## Funcionalidades da versão 1.0

### Gerenciamento de membros

Permite cadastrar, consultar e gerenciar os membros da organização, incluindo seus dados pessoais e vínculos institucionais.

O sistema contempla diferentes funções, como aluno, professor, músico, maestro e secretário.

### Convites e autenticação

Permite convidar membros por e-mail para acessar a plataforma, além de oferecer autenticação e controle de acesso aos recursos do sistema.

### Gerenciamento do acervo musical

Permite cadastrar músicas, instrumentos e partituras, disponibilizando o material musical de forma centralizada.

Uma música pode possuir diferentes partituras, de acordo com os instrumentos utilizados em sua execução.

### Acesso por perfil

O sistema disponibiliza áreas e recursos conforme os vínculos e permissões dos participantes.

## Evolução planejada

### Gestão acadêmica — Fevereiro de 2027

O módulo de gestão acadêmica encontra-se em desenvolvimento e não integra o escopo da primeira entrega.

Estão previstas funcionalidades como:

- Gerenciamento de turmas e salas.
- Organização de horários.
- Vinculação de professores e alunos às turmas.
- Gerenciamento de aulas.
- Registro de chamadas e frequência.

A disponibilização dessas funcionalidades dependerá da conclusão dos testes, da revisão das regras de negócio e da validação com a organização.

## Arquitetura

O sistema utiliza uma arquitetura monolítica, com separação entre cliente e servidor.

O backend é organizado em camadas lógicas, incluindo controladores REST, serviços de aplicação, entidades, repositórios e objetos de transferência de dados (DTOs).

O frontend é desenvolvido como uma aplicação React, organizada em componentes, páginas, rotas, hooks e serviços de integração com a API.

## Tecnologias

**Backend**
- Java 25
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- Flyway
- PostgreSQL
- Maven

**Frontend**
- TypeScript
- React
- Vite
- TanStack Router
- TanStack Query
- Tailwind CSS
- Node.js 22

**Infraestrutura**
- Docker e Docker Compose
- PostgreSQL
- Armazenamento local de arquivos
- SMTP para envio de convites

## Documentação

- [Quick Start](docs/QUICK_START.md) — Instalação e execução do projeto.
- [Configuração](docs/CONFIGURATION.md) — Variáveis de ambiente e perfis da aplicação.

## Contexto acadêmico

Projeto desenvolvido como atividade da disciplina **Projeto Integrador: Sistema Web**, em colaboração com a Banda Santa Bárbara.

A primeira entrega prioriza os problemas identificados durante o levantamento de requisitos, enquanto funcionalidades adicionais serão consideradas em futuras versões.
