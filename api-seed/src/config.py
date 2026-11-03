import os

from dotenv import load_dotenv

load_dotenv()

BASE_URL = os.getenv("API_BASE_URL")
API_VERSION = os.getenv("API_VERSION")

ADMIN_LOGIN = os.getenv("ADMIN_LOGIN")
ADMIN_PASSWORD = os.getenv("ADMIN_PASSWORD")

if not all([BASE_URL, API_VERSION, ADMIN_LOGIN, ADMIN_PASSWORD]):
    raise RuntimeError("Variáveis de ambiente obrigatórias não configuradas.")