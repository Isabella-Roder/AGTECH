import { apiFetch, setToken } from "./cliente";

interface LoginResposta {
    token: string;
}

export async function login(email: string, senha: string): Promise<void> {
    const resposta = await apiFetch<LoginResposta>("/auth/login", {
        method: "POST",
        body: JSON.stringify({email, senha}),
    });

    setToken(resposta.token);
}