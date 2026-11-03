
import random
import requests

from membros.membros_uri import MEMBROS_URI, MEMBROS_URI_EXECUTAVEL
from faker import Faker
from datetime import date

fake = Faker("pt_BR")


class MembroSeed:

    def __init__(self):
        self.sexo = ["MASCULINO", "FEMININO"]

    

    def executar_seed_transformar_30_primeiros_membros_menor_sem_acesso_em_alunos(self, session):

        if MEMBROS_URI_EXECUTAVEL['vincular-alunos'] is False:
            print("=" * 50)
            print("SEED PULADA, NÃO INCLUSA")
            print(f"Endpoint: {MEMBROS_URI['vincular']}")
            print("=" * 50)
            return
        

        params = {
            "maiorDeIdade": False,
            "statusCredencialAcesso": "SEM_ACESSO",
            "page": 0,
            "size": 30
        }

        try:
            response = session.get(
                MEMBROS_URI["consultar"],
                params=params,
                timeout=15
            )

            if not response.ok:
                print(f"ERRO AO CONSULTAR MEMBROS: {response.status_code}")
                print(response.text)
                return

            membros = response.json().get("content", [])

            print(f"\nMEMBROS ENCONTRADOS: {len(membros)}")

            sucessos = 0
            falhas = 0

            for numero, membro in enumerate(membros, start=1):
                id_membro = membro["id"]
                nome = membro["nome"]

                uri = MEMBROS_URI["vincular"].format(id=id_membro)

                dados = {
                    "dataInicio": date.today().strftime("%d/%m/%Y"),
                    "funcao": "ALUNO"
                }

                try:
                    response = session.post(
                        uri,
                        json=dados,
                        timeout=15
                    )

                    if response.ok:
                        sucessos += 1
                        print(
                            f"[{numero}] SUCESSO: "
                            f"{nome} recebeu a função ALUNO."
                        )
                    else:
                        falhas += 1
                        print(
                            f"[{numero}] ERRO {response.status_code}: {nome}"
                        )
                        print(response.text)

                except requests.RequestException as erro:
                    falhas += 1
                    print(f"[{numero}] ERRO DE REQUISIÇÃO: {nome}")
                    print(erro)

            print("\nRESULTADO DOS VÍNCULOS")
            print(f"Sucessos: {sucessos}")
            print(f"Falhas: {falhas}")

        except requests.RequestException as erro:
            print(f"ERRO AO CONSULTAR MEMBROS: {erro}")

    """
    Método em lote que cadastra vários membros menores.
    """
    def executar_seed_cadastrar_membro_menores(self, quantidade, session):

        if MEMBROS_URI_EXECUTAVEL['cadastrar-menor'] is False:
            print("=" * 50)
            print("SEED PULADA, NÃO INCLUSA")
            print(f"Endpoint: {MEMBROS_URI['cadastrar-menor']}")
            print("=" * 50)
            return


        print("=" * 50)
        print("INICIANDO SEED DE MEMBROS MENORES")
        print("=" * 50)

        self.cadastrar_membro_menor(session, 1, quantidade)

        print("\nINICIANDO VÍNCULOS DE RESPONSÁVEIS")

        self.vincular_funcao_responsavel_com_data_inicio_hoje(
            session,
            1,
            quantidade
        )

    """
    Busca menores na página 0, consulta seus responsáveis
    e cria a função RESPONSAVEL para cada responsável encontrado.
    """
    def vincular_funcao_responsavel_com_data_inicio_hoje(
        self, session, numero, quantidade
    ):

        print("\nBUSCANDO MENORES PARA IDENTIFICAR RESPONSÁVEIS")

        try:
            response = session.get(
                MEMBROS_URI["consultar"],
                params={
                    "maiorDeIdade": False,
                    "page": 0,
                    "size": 30
                },
                timeout=15
            )

            response.raise_for_status()
            dados = response.json()

            menores = dados["content"][:quantidade]

        except requests.RequestException as erro:
            print(f"ERRO AO CONSULTAR MENORES: {erro}")
            return

        except (KeyError, ValueError) as erro:
            print(f"ERRO AO INTERPRETAR MENORES: {erro}")
            return

        if not menores:
            print("NENHUM MENOR ENCONTRADO NA PÁGINA 0.")
            return

        ids_menores = [
            menor["id"]
            for menor in menores
        ]

        responsaveis = self.pegar_responsaveis_dos_menores(
            session,
            ids_menores
        )

        if not responsaveis:
            print("NENHUM RESPONSÁVEL ENCONTRADO.")
            return

        hoje = date.today().strftime("%d/%m/%Y")

        sucessos = 0
        falhas = 0

        print(f"\nRESPONSÁVEIS ENCONTRADOS: {len(responsaveis)}")

        for indice, responsavel in enumerate(responsaveis, start=numero):

            id_responsavel = responsavel["id"]

            try:
                response = session.post(
                    MEMBROS_URI["vincular"].format(
                        id=id_responsavel
                    ),
                    json={
                        "dataInicio": hoje,
                        "funcao": "RESPONSAVEL"
                    },
                    timeout=15
                )

                if response.ok:
                    print(
                        f"[{indice}] SUCESSO: "
                        f"{responsavel['nome']} "
                        f"recebeu a função RESPONSAVEL."
                    )
                    sucessos += 1

                else:
                    print(
                        f"[{indice}] ERRO {response.status_code}: "
                        f"{responsavel['nome']}"
                    )
                    print(response.text)
                    falhas += 1

            except requests.RequestException as erro:
                print(f"[{indice}] ERRO NA REQUISIÇÃO: {erro}")
                falhas += 1

        print("\n" + "=" * 50)
        print("RESULTADO DOS VÍNCULOS")
        print(f"Sucessos: {sucessos}")
        print(f"Falhas: {falhas}")
        print("=" * 50)

    """
    Consulta os responsáveis de cada menor.
    Retorna uma lista de responsáveis sem IDs duplicados.
    """
    def pegar_responsaveis_dos_menores(self, session, idMenores):

        responsaveis = {}
        
        for id_menor in idMenores:

            try:
                response = session.get(
                    MEMBROS_URI["consultar-responsaveis"].format(
                        id=id_menor
                    ),
                    timeout=15
                )

                response.raise_for_status()

                dados = response.json()

                for responsavel in dados.get("responsaveis", []):

                    id_responsavel = responsavel["id"]

                    if id_responsavel not in responsaveis:
                        responsaveis[id_responsavel] = responsavel

            except requests.RequestException as erro:
                print(
                    f"ERRO AO CONSULTAR RESPONSÁVEIS DO MENOR "
                    f"{id_menor}: {erro}"
                )

            except (KeyError, ValueError) as erro:
                print(
                    f"ERRO AO INTERPRETAR RESPONSÁVEIS "
                    f"DO MENOR {id_menor}: {erro}"
                )

        return list(responsaveis.values())

    """
    Cadastra membros menores usando adultos encontrados
    na página 0 da consulta de membros.
    """
    def cadastrar_membro_menor(self, session, numero, quantidade):

        print("\nBUSCANDO ADULTOS PARA VINCULAR AOS MENORES")

        try:
            response = session.get(
                MEMBROS_URI["consultar"],
                params={
                    "maiorDeIdade": True,
                    "page": 0,
                    "size": 30
                },
                timeout=15
            )

            response.raise_for_status()

            dados = response.json()

            adultos = dados["content"][:quantidade]

        except requests.RequestException as erro:
            print(f"ERRO AO CONSULTAR ADULTOS: {erro}")
            return

        except (KeyError, ValueError) as erro:
            print(f"ERRO AO INTERPRETAR ADULTOS: {erro}")
            return

        if len(adultos) < quantidade:
            print(
                f"ERRO: Foram encontrados apenas {len(adultos)} "
                f"adultos na página 0. Quantidade solicitada: {quantidade}."
            )
            return

        sucessos = 0
        falhas = 0

        print(f"ADULTOS ENCONTRADOS: {len(adultos)}")

        for indice, adulto in enumerate(adultos, start=numero):

            id_responsavel = adulto["id"]

            membro = {
                "menor": {
                    "nome": fake.name(),
                    "email": fake.unique.email(),
                    "telefone": self.gerar_telefone(),
                    "endereco": fake.address().replace("\n", ", "),
                    "sexo": random.choice(self.sexo),
                    "dataNascimento": fake.date_of_birth(
                        minimum_age=1,
                        maximum_age=17
                    ).strftime("%d/%m/%Y")
                },
                "idResponsavel": id_responsavel,
                "grauRelacao": "PROGENITORES"
            }

            try:
                response = session.post(
                    MEMBROS_URI["cadastrar-menor"],
                    json=membro,
                    timeout=15
                )

                if response.ok:
                    print(
                        f"[{indice}] SUCESSO: "
                        f"{membro['menor']['nome']} "
                        f"| Responsável: {adulto['nome']}"
                    )
                    sucessos += 1

                else:
                    print(
                        f"[{indice}] ERRO {response.status_code}: "
                        f"{membro['menor']['nome']}"
                    )
                    print(response.text)
                    falhas += 1

            except requests.RequestException as erro:
                print(f"[{indice}] ERRO NA REQUISIÇÃO: {erro}")
                falhas += 1

        print("\n" + "=" * 50)
        print("RESULTADO DO CADASTRO DE MENORES")
        print(f"Sucessos: {sucessos}")
        print(f"Falhas: {falhas}")
        print("=" * 50)

    """
    Gera um telefone aleatório.
    """
    def gerar_telefone(self):

        ddd = random.choice(["48", "47", "49", "11", "21", "41"])
        numero = random.randint(900000000, 999999999)

        return f"{ddd}{numero}"



    """
    Método em lote que cadastrar vários membros de mairoes
    """
    def executar_seed_cadastrar_membro_maiores(self, quantidade, session):
        sucessos = 0
        falhas = 0

        if MEMBROS_URI_EXECUTAVEL['cadastrar'] is False:
            print("=" * 50)
            print("SEED PULADA, NÃO INCLUSA")
            print(f"Endpoint: {MEMBROS_URI['cadastrar']}")
            print("=" * 50)
            return

        print("=" * 50)
        print("INICIANDO SEED DE MEMBROS MAIORES")
        print(f"Quantidade planejada: {quantidade}")
        print(f"Endpoint: {MEMBROS_URI['cadastrar']}")
        print("=" * 50)

        for numero in range(1, quantidade + 1):
            resultado = self.cadastrar_membro_maior(
                session,
                numero,
                quantidade
            )

            if resultado:
                sucessos += 1
            else:
                falhas += 1

        print("\n" + "=" * 50)
        print("RESULTADO DA SEED")
        print(f"Sucessos: {sucessos}")
        print(f"Falhas: {falhas}")
        print(f"Total processado: {sucessos + falhas}")
        print("=" * 50)



    """
    Método que cadastrar um membro
    """
    def cadastrar_membro_maior(self, session, numero, quantidade):
        membro = self.gerar_membro()

        try:
            response = session.post(
                MEMBROS_URI['cadastrar'],
                json=membro,
                timeout=15
            )

            if response.ok:
                print(
                    f"[{numero}/{quantidade}] "
                    f"SUCESSO: {membro['nome']}"
                )

                return True

            print(
                f"[{numero}/{quantidade}] "
                f"ERRO {response.status_code}: "
                f"{membro['nome']}"
            )

            print(response.text)

            return False

        except requests.RequestException as erro:
            print(
                f"[{numero}/{quantidade}] "
                f"FALHA NA REQUISIÇÃO: {erro}"
            )

            return False


    def gerar_membro(self):
        nome = fake.name()

        return {
            "nome": nome,
            "email": fake.unique.email(),
            "telefone": self.gerar_telefone(),
            "endereco": fake.address().replace("\n", ", "),
            "sexo": random.choice(self.sexo),
            "dataNascimento": self.gerar_data_nascimento()
        }
        
    """
        Gera uma data de nascimento entre 18 e 60 anos atrás.
        Assim, todos os membros serão maiores de idade.
    """
    def gerar_data_nascimento(self):
        nascimento = fake.date_of_birth(
            minimum_age=18,
            maximum_age=60
        )

        return nascimento.strftime("%d/%m/%Y")


    """
        Gera um telefone celular brasileiro fictício.
    """
    def gerar_telefone(self): 
        ddd = random.choice([
            "48", "47", "49", "11", "21", "41"
        ])

        numero = random.randint(900000000, 999999999)

        return f"{ddd}{numero}"

    

    
