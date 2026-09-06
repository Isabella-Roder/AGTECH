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


def criar_talhao(token: str, nome: str, area_hectares: float, propriedadeId: str) -> dict :
    resposta = requests.post(
        f"{BASE_URL}/api/propriedades/{propriedadeId}/talhoes",
        json={
            "nome": nome,
            "areaHectares": area_hectares
        },
        headers={
            "Authorization": f"Bearer {token}"
        }
    )

    resposta.raise_for_status()

    return resposta.json()

def criar_cultura(token: str, nome: str) -> dict :
    resposta = requests.post(
        f"{BASE_URL}/api/culturas",
        json={
            "nome": nome
        },
        headers={
            "Authorization": f"Bearer {token}"
        }
    )

    if resposta.status_code == 422 :
        print(f"Cultura já existe: {nome}")
        return resposta.json()

    resposta.raise_for_status()

    return resposta.json()


def criar_safra(token: str, propriedadeId: str, talhaoId: str, culturaId: str, nome: str, data_fim_prevista) -> dict :
    resposta = requests.post(
        f"{BASE_URL}/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras", 
        json= {
            "culturaId": culturaId,
            "nome": nome,
            "dataFimPrevista": data_fim_prevista
        },
        headers= {
            "Authorization": f"Bearer {token}"
        }
    )

    resposta.raise_for_status()

    return resposta.json()


def iniciar_safra(token: str, propriedadeId: str, talhaoId: str, safraId: str) -> dict :
    resposta = requests.patch(
        f"{BASE_URL}/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras/{safraId}/iniciar",
        headers= {
            "Authorization": f"Bearer {token}"
        }
    )

    resposta.raise_for_status()

    return resposta.json()


def criar_plantio(token: str, propriedadeId: str, talhaoId: str, safraId: str, data_plantio: str, area_plantada_hectares: float, observacoes: str) -> dict :
    resposta = requests.post(
        f"{BASE_URL}/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras/{safraId}/plantios", 
        json={
            "dataPlantio": data_plantio,
            "areaPlantadaHectares": area_plantada_hectares,
            "observacoes": observacoes
        },
        headers={
            "Authorization": f"Bearer {token}"
        }
    )

    resposta.raise_for_status()

    return resposta.json()


if __name__ == "__main__" :
    cadastrar_usuario("Isabella", "isa@agtech.com", "12345678")

    token = login("isa@agtech.com", "12345678")
    print(f"Token obtido: {token[:20]}...")

    propriedade = criar_propriedade(token, "Fazenda Isabella", "Uberlandia", "MG", 75.5)

    print(f"Propriedade criada: {propriedade}")

    talhao = criar_talhao(token, "Talhão sul", 12.5, propriedade["id"])
    print(f"Talhão criado: {talhao}")

    cultura = criar_cultura(token, "Milho")
    print(f"Cultura criada: {cultura}")

    safra = criar_safra(token, propriedade["id"], talhao["id"], cultura["id"], "Safra de Milho 2026/2027", "2027-08-17")
    print(f"Safra criada: {safra}")

    safra = iniciar_safra(token, propriedade["id"], talhao["id"], safra["id"])
    print(f"Safra iniciada: {safra}")

    plantio = criar_plantio(token, propriedade["id"], talhao["id"], safra["id"], "2026-09-06", 6.5, "Plantação de milhos")
    print(f"Plantio criado: {plantio}")