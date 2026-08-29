import { apiFetch } from "./cliente";

export interface Propriedade {
    id: number;
    nome: string;
    municipio: string;
    estado: string;
    areaTotalHectares: number;
    ativo: boolean;
}

export interface CadastroPropriedade {
    nome: string;
    municipio: string;
    estado: string;
    areaTotalHectares: number
}

export function listarMinhasPropriedades(): Promise<Propriedade[]> {
    return apiFetch<Propriedade[]>("/api/propriedades/minhas");
}

export function cadastrarPropriedade(dados: CadastroPropriedade,): Promise<Propriedade> {
    return apiFetch<Propriedade>("/api/propriedades", {
        method: "POST",
        body: JSON.stringify(dados),
    });
}