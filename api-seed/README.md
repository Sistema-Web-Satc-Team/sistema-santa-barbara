# Como usar

Observe esses códigos não pertencem ao software real, serve apenas para em desenvolvimento adicionarmos
dados rapidamente.

## Dependências necessárias:

1. Instalar o python em sua máquina
2. Adicionar a lib de requests: `pip install requests`
3. Adicionar a lib de faker: `pip install Faker`
4. Adicionar a lib de python-dotenv: `pip install python-dotenv`

## Requisitos de uso

- Certifique-se de que a API backend esteja rodando na porta `localhost:8080` e que você já inicou o sistema após cadastrar um super administrador na rota `POST localhost:8080/api/v1/setup`.

- Crie um `.env` no diretório `api-seed` e adicione as variaveis `ADMIN_LOGIN` e `ADMIN_PASSWORD` com seus respectivos valores.

- Em `src` cada pasta corresponde ao módulo todos terão um `<recurso>_uri.py`, nele você configura quais rotas deseja executar ou não.

## Execução

- No diretório `api-seed` execute o comando `python src/main.py`