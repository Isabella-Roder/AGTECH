import { apiFetch } from "./cliente";

export type SexoAnimal = "MACHO" | "FEMEA";

export interface Animal {
    id: string;
    rebanhoId: string;
    identificacao: string;
    sexo: SexoAnimal;
    dataNascimento: string | null;
    ativo: boolean;
}

export interface DadosAnimal {
    identificacao: string;
    sexo: SexoAnimal;
    dataNascimento: string | null;
}

const caminhoAnimais = (propriedadeId: string, rebanhoId: string) =>
    `/api/propriedades/${propriedadeId}/rebanhos/${rebanhoId}/animais`;

export function listarAnimais(propriedadeId: string, rebanhoId: string): Promise<Animal[]> {
    return apiFetch<Animal[]>(caminhoAnimais(propriedadeId, rebanhoId));
}

export function buscarAnimalPorId(propriedadeId: string, rebanhoId: string, animalId: string): Promise<Animal> {
    return apiFetch<Animal>(`${caminhoAnimais(propriedadeId, rebanhoId)}/${animalId}`);
}

export function cadastrarAnimal(propriedadeId: string, rebanhoId: string, dados: DadosAnimal): Promise<Animal> {
    return apiFetch<Animal>(caminhoAnimais(propriedadeId, rebanhoId), {
        method: "POST",
        body: JSON.stringify(dados)
    });
}

export function atualizarAnimal(propriedadeId: string, rebanhoId: string, animalId: string, dados: DadosAnimal): Promise<Animal> {
    return apiFetch<Animal>(`${caminhoAnimais(propriedadeId, rebanhoId)}/${animalId}`, {
        method: "PUT",
        body: JSON.stringify(dados)
    });
}

export function ativarAnimal(propriedadeId: string, rebanhoId: string, animalId: string): Promise<Animal> {
    return apiFetch<Animal>(`${caminhoAnimais(propriedadeId, rebanhoId)}/${animalId}/ativar`, {
        method: "PATCH"
    });
}

export function desativarAnimal(propriedadeId: string, rebanhoId: string, animalId: string): Promise<Animal> {
    return apiFetch<Animal>(`${caminhoAnimais(propriedadeId, rebanhoId)}/${animalId}/desativar`, {
        method: "PATCH"
    });
}

export const rotulosSexoAnimal: Record<SexoAnimal, string> = {
    MACHO: "Macho",
    FEMEA: "Fêmea"
};