import { apiFetch } from "./cliente";

export interface Abastecimento {
    id: string;
    maquinaId: string;
    data: string;
    litros: number;
    horimetroNoMomento: number;
    observacoes: string | null;
}

export interface CadastroAbastecimento {
    data: string;
    litros: number;
    horimetroNoMomento: number;
    observacoes: string | null;
}

const caminhoAbastecimentos = (
    propriedadeId: string,
    maquinaId: string
) =>
    `/api/propriedades/${propriedadeId}/maquinas/${maquinaId}/abastecimentos`;

export function listarAbastecimento(propriedadeId: string, maquinaId: string): Promise<Abastecimento[]> {
    return apiFetch<Abastecimento[]>(caminhoAbastecimentos(propriedadeId, maquinaId));
}

export function buscarAbastecimentoPorId(propriedadeId: string, maquinaId: string, abastecimentoId: string): Promise<Abastecimento> {
    return apiFetch<Abastecimento>(`${caminhoAbastecimentos(propriedadeId, maquinaId)}/${abastecimentoId}`);
}

export function cadastrarAbastecimento(propriedadeId: string, maquinaId: string, dados: CadastroAbastecimento): Promise<Abastecimento> {
    return apiFetch<Abastecimento>(caminhoAbastecimentos(propriedadeId, maquinaId), {
        method: "POST",
        body: JSON.stringify(dados)
    });
}

export function atualizarAbastecimento(propriedadeId: string, maquinaId: string, abastecimentoId: string, dados: CadastroAbastecimento): Promise<Abastecimento> {
    return apiFetch<Abastecimento>(`${caminhoAbastecimentos(propriedadeId, maquinaId)}/${abastecimentoId}`, {
        method: "PUT",
        body: JSON.stringify(dados)
    });
}