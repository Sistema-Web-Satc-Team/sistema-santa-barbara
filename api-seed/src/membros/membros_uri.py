from config import BASE_URL, API_VERSION

BASE = f"{BASE_URL}/{API_VERSION}/membros"

MEMBROS_URI = {
    "cadastrar": BASE,
    "cadastrar-menor": f"{BASE}/menores",
    "consultar": BASE,
    "vincular": f"{BASE}/{{id}}/vinculos",
    "consultar-responsaveis": f"{BASE}/{{id}}/responsaveis"
}

MEMBROS_URI_EXECUTAVEL = {
    "cadastrar": False,
    "cadastrar-menor": False,
    "consultar": True,
    "vincular-alunos": True
}