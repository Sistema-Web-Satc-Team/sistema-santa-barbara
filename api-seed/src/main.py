import requests

from config import ADMIN_LOGIN, ADMIN_PASSWORD
from auth.auth import Auth
from membros.membro_seed import MembroSeed


if __name__ == "__main__":

    with requests.Session() as session:

        auth = Auth()

        autenticado = auth.executar_login(
            session,
            ADMIN_LOGIN,
            ADMIN_PASSWORD
        )

        if autenticado:

            membro_seed = MembroSeed()

            membro_seed.executar_seed_cadastrar_membro_maiores(
                70,
                session
            )

            membro_seed.executar_seed_cadastrar_membro_menores(
                30,
                session
            )

            membro_seed.executar_seed_transformar_30_primeiros_membros_menor_sem_acesso_em_alunos(session)