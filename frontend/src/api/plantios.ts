import { apiFetch } from "./cliente";

export interface Plantio {
    id: string;
    safraId: string;
    dataPlantio: string;
    areaPlantadaHectares: number;
    observacoes: string | null;
}

export interface CadastroPlantio {
    dataPlantio: string;
    areaPlantadaHectares: number;
    observacoes: string | null;
}

const caminhoPlantio = (propriedadeId: string, talhaoId: string, safraId: string) =>
    `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras/${safraId}/plantios`;

export function listarPlantio(propriedadeId: string, talhaoId: string, safraId: string): Promise<Plantio[]> {
    return apiFetch<Plantio[]> (
        caminhoPlantio(propriedadeId, talhaoId, safraId)
    );
}

export function cadastrarPlantio(propriedadeId: string, talhaoId: string, safraId: string, dados: CadastroPlantio): Promise<Plantio> {
    return apiFetch<Plantio> (
        caminhoPlantio(propriedadeId, talhaoId, safraId), {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function atualizarPlantio(propriedadeId: string, talhaoId: string, safraId: string, plantioId: string, dados: CadastroPlantio): Promise<Plantio> {
    return apiFetch<Plantio> (
        `${caminhoPlantio(propriedadeId, talhaoId, safraId)}/${plantioId}`, {
            method: "PUT",
            body: JSON.stringify(dados)
        }
    );
}

export function buscarPlantioPorId(propriedadeId: string, talhaoId: string, safraId: string, plantioId: string): Promise<Plantio> {
    return apiFetch<Plantio> (
        `${caminhoPlantio(propriedadeId, talhaoId, safraId)}/${plantioId}`
    );
}
