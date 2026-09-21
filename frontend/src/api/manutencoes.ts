import { apiFetch } from "./cliente";

export type TipoManutencao = "PREVENTIVA" | "CORRETIVA";

export interface Manutencao {
    id: string;
    maquinaId: string;
    tipo: TipoManutencao;
    dataRealizacao: string;
    horimetroNoMomento: number;
    descricao: string | null;
}

export interface CadastroManutencao {
    tipo: TipoManutencao;
    dataRealizacao: string;
    horimetroNoMomento: number;
    descricao: string | null;
}

const caminhoManutencoes = (
    propriedadeId: string,
    maquinaId: string
) =>
    `/api/propriedades/${propriedadeId}/maquinas/${maquinaId}/manutencoes`;

export function listarManutencoes(propriedadeId: string, maquinaId: string): Promise<Manutencao[]> {
    return apiFetch<Manutencao[]>(caminhoManutencoes(propriedadeId, maquinaId));
}

export function buscarManutencaoPorId(propriedadeId: string, maquinaId: string, manutencaoId: string): Promise<Manutencao> {
    return apiFetch<Manutencao>(`${caminhoManutencoes(propriedadeId, maquinaId)}/${manutencaoId}`);
}

export function cadastroManutencao(propriedadeId: string, maquinaId: string, dados: CadastroManutencao): Promise<Manutencao> {
    return apiFetch<Manutencao>(
        caminhoManutencoes(propriedadeId, maquinaId), {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function atualizarManutencao(
    propriedadeId: string,
    maquinaId: string,
    manutencaoId: string,
    dados: CadastroManutencao
): Promise<Manutencao> {
    return apiFetch<Manutencao>(
        `${caminhoManutencoes(propriedadeId, maquinaId)}/${manutencaoId}`, {
            method: "PUT",
            body: JSON.stringify(dados)
        }
    );
}

export const rotulosTipoManutencao: Record<TipoManutencao, string> = {
    PREVENTIVA: "Preventiva",
    CORRETIVA: "Corretiva"
};