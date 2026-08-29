import { apiFetch } from "./cliente";

export interface Propriedade {
    id: number;
    nome: string;
    municipio: string;
    estado: string;
    areaTotalHectares: number;
    ativo: boolean;
}

export function listarMinhasPropriedades(): Promise<Propriedade[]> {
    return apiFetch<Propriedade[]>("/api/propriedades/minhas");
}