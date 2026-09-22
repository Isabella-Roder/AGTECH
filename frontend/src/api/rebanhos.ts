import { apiFetch } from "./cliente";

export type EspecieAnimal =
    | "BOVINO"
    | "SUINO"
    | "OVINO"
    | "CAPRINO"
    | "AVE";

export interface Rebanho {
    id: string;
    propriedadeId: string;
    nome: string;
    especie: EspecieAnimal;
    ativo: boolean;
}

export interface DadosRebanho {
    nome: string;
    especie: EspecieAnimal;
}

const caminhoRebanhos = (propriedadeId: string) =>
    `/api/propriedades/${propriedadeId}/rebanhos`;

export function listarRebanhos(propriedadeId: string): Promise<Rebanho[]> {
    return apiFetch<Rebanho[]>(caminhoRebanhos(propriedadeId));
}

export function buscarRebanhoPorId(propriedadeId: string, rebanhoId: string): Promise<Rebanho> {
    return apiFetch<Rebanho>(`${caminhoRebanhos(propriedadeId)}/${rebanhoId}`);
}

export function cadastrarRebanho(propriedadeId: string, dados: DadosRebanho): Promise<Rebanho> {
    return apiFetch<Rebanho>(caminhoRebanhos(propriedadeId), {
        method: "POST",
        body: JSON.stringify(dados),
    });
}

export function atualizarRebanho(propriedadeId: string, rebanhoId: string, dados: DadosRebanho): Promise<Rebanho> {
    return apiFetch<Rebanho>(`${caminhoRebanhos(propriedadeId)}/${rebanhoId}`, {
        method: "PUT",
        body: JSON.stringify(dados)
    });
}

export function ativarRebanho(propriedadeId: string, rebanhoId: string): Promise<Rebanho> {
    return apiFetch<Rebanho>(`${caminhoRebanhos(propriedadeId)}/${rebanhoId}/ativar`, {
        method: "PATCH"
    });
}

export function desativarRebanho(propriedadeId: string, rebanhoId: string) {
    return apiFetch<Rebanho>(`${caminhoRebanhos(propriedadeId)}/${rebanhoId}/desativar`, {
        method: "PATCH"
    });
}

export const rotulosEspecie: Record<EspecieAnimal, string> = {
    BOVINO: "Bovinos",
    SUINO: "Suínos",
    OVINO: "Ovinos",
    CAPRINO: "Caprinos",
    AVE: "Aves"
};