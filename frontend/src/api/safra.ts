import { apiFetch } from "./cliente";

export type StatusSafra =
    | "PLANEJADA"
    | "EM_ANDAMENTO"
    | "FINALIZADA"
    | "CANCELADA";

export interface Safra {
    id: string;
    talhaoId: string;
    culturaId: string;
    nome: string;
    dataInicio: string;
    dataFimPrevisto: string;
    dataFimReal: string | null;
    status: StatusSafra;
}

export interface CadastroSafra {
    culturaId: string;
    nome: string;
    dataFimPrevista: string;
}

const caminhoSafras = (propriedadeId: string, talhaoId: string) =>
    `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}/safras`;

export function listarSafras(
    propriedadeId: string,
    talhaoId: string,
): Promise<Safra[]> {
    return apiFetch<Safra[]>(caminhoSafras(propriedadeId, talhaoId));
}

export function cadastrarSafra(
    propriedadeId: string,
    talhaoId: string,
    dados: CadastroSafra,
): Promise<Safra> {
    return apiFetch<Safra>(caminhoSafras(propriedadeId, talhaoId), {
        method: "POST",
        body: JSON.stringify(dados),
    });
}

function alterarStatusSafra(
    propriedadeId: string,
    talhaoId: string,
    safraId: string,
    acao: "iniciar" | "finalizar" | "cancelar",
): Promise<Safra> {
    return apiFetch<Safra>(
        `${caminhoSafras(propriedadeId, talhaoId)}/${safraId}/${acao}`,
        { method: "PATCH" },
    );
}

export const iniciarSafra = (propriedadeId: string, talhaoId: string, safraId: string) =>
    alterarStatusSafra(propriedadeId, talhaoId, safraId, "iniciar");

export const finalizarSafra = (propriedadeId: string, talhaoId: string, safraId: string) =>
    alterarStatusSafra(propriedadeId, talhaoId, safraId, "finalizar");

export const cancelarSafra = (propriedadeId: string, talhaoId: string, safraId: string) =>
    alterarStatusSafra(propriedadeId, talhaoId, safraId, "cancelar");

export function buscarSafraPorId(
    propriedadeId: string,
    talhaoId: string,
    safraId: string,
): Promise<Safra> {
    return apiFetch<Safra>(
        `${caminhoSafras(propriedadeId, talhaoId)}/${safraId}`,
    );
}
