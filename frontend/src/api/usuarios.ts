import { apiFetch } from "./cliente";

export interface Usuario {
    id: number;
    nome: string;
    email: string;
    ativo: boolean;
}

interface CadastroUsuario {
    nome: string;
    email: string;
    senha: string;
}

export function cadastrarUsuario(dados: CadastroUsuario,): Promise<Usuario> {
    return apiFetch<Usuario>("/api/usuarios", {
        method: "POST",
        body: JSON.stringify(dados),
    });
}