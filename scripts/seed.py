import requests

BASE_URL = "http://localhost:8080"

def cadastrar_usuario(nome: str, email: str, senha: str) -> None :
    resposta = requests.post(
        f"{BASE_URL}/api/usuarios",
        json={"nome": nome, "email": email, "senha": senha}
    )

    if resposta.status_code == 201 :
        print(f"Usuário criado E-mail: {email}")
    elif resposta.status_code == 422 :
        print(f"Usuário já existe: {email}")
    else :
        resposta.raise_for_status()


def login(email: str, senha: str) -> str :
    resposta = requests.post(
        f"{BASE_URL}/auth/login",
        json={"email": email, "senha": senha}
    )

    resposta.raise_for_status()

    return resposta.json()["token"]


def criar_propriedade(
        token: str,
        nome: str,
        municipio: str,
        estado: str,
        area_hectares: float
) -> dict :
    resposta = requests.post(
        f"{BASE_URL}/api/propriedades",
        json={
            "nome": nome,
            "municipio": municipio,
            "estado": estado,
            "areaTotalHectares": area_hectares,
        },
        headers={"Authorization": f"Bearer {token}"},
    )

    resposta.raise_for_status()

    return resposta.json()


if __name__ == "__main__" :
    cadastrar_usuario("Isabella", "isa@agtech.com", "12345678")

    token = login("isa@agtech.com", "12345678")
    print(f"Token obtido: {token[:20]}...")

    propriedade = criar_propriedade(token, "Fazenda Isabella", "Uberlandia", "MG", 75.5)

    print(f"Propriedade criada: {propriedade}")