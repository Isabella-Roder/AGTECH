import { apiFetch } from "./cliente";

export interface Cultura {
    id: number;
    nome: string;
}

export interface CadastroCultura {
    nome: string;
}

export function cadastrarCultura(dados: CadastroCultura): Promise<Cultura> {
    return apiFetch<Cultura>(
        "/api/culturas",
        {
            method: "POST",
            body: JSON.stringify(dados),
        },
    );
}

export function listarCulturas(): Promise<Cultura[]> {
    return apiFetch<Cultura[]>("/api/culturas");
}
