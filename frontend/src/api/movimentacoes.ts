import { apiFetch } from "./cliente";

export type TipoMovimentacao = "ENTRADA" | "SAIDA";

export interface MovimentacaoEstoque {
    id: string;
    produtoId: string;
    depositoId: string;
    tipo: TipoMovimentacao;
    quantidade: number;
    data: string;
    safraId: string | null;
    observacoes: string | null;
}

export interface CadastroMovimentacaoEstoque {
    produtoId: string;
    tipo: TipoMovimentacao;
    quantidade: number;
    data: string;
    safraId: string | null;
    observacoes: string | null;
}

function caminhoMovimentacoes(propriedadeId: string, depositoId: string) {
    return `/api/propriedades/${propriedadeId}/depositos/${depositoId}/movimentacoes`;
}

export function listarMovimentacoes(propriedadeId: string, depositoId: string): Promise<MovimentacaoEstoque[]> {
    return apiFetch<MovimentacaoEstoque[]> (
        caminhoMovimentacoes(propriedadeId, depositoId)
    );
}

export function cadastrarMovimentacao(propriedadeId: string, depositoId: string, dados: CadastroMovimentacaoEstoque): Promise<MovimentacaoEstoque> {
    return apiFetch<MovimentacaoEstoque> (
        caminhoMovimentacoes(propriedadeId, depositoId), {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function buscarMovimentacaoPorId(propriedadeId: string, depositoId: string, movimentacaoId: string): Promise<MovimentacaoEstoque> {
    return apiFetch<MovimentacaoEstoque> (
        `${caminhoMovimentacoes(propriedadeId, depositoId)}/${movimentacaoId}`
    );
}
