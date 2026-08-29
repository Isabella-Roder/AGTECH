const API_BASE_URL = "http://localhost:8080";

export function getToken(): string | null {
    return localStorage.getItem("token");
}

export function setToken(token: string): void {
    localStorage.setItem("token", token);
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
    const token = getToken();

    const headers: HeadersInit = {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
    };

    const resposta = await fetch(`${API_BASE_URL}${path}`, {...options, headers});

    if (!resposta.ok) {
        const erro = await resposta.json().catch(() => null);
        throw new Error(erro?.mensagem ?? `Erro ${resposta.status}`);
    }

    if (resposta.status === 204) {
        return undefined as T;
    }

    return resposta.json();
}

export function removerToken(): void {
    localStorage.removeItem("token");
}