import requests

from auth.auth_uri import AUTH_URI

import requests

from auth.auth_uri import AUTH_URI


class Auth:

    def executar_login(self, session, email, senha):

        response = session.post(
            AUTH_URI["login"],
            json={
                "login": email,
                "senha": senha
            },
            timeout=15
        )

        if not response.ok:
            print(f"Falha no login: {response.status_code}")
            print(response.text)
            return False

        print("Login realizado com sucesso!")

        print("Cookies recebidos:", session.cookies.get_dict())

        return True