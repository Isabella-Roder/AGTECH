import { apiFetch } from "./cliente";

export interface Talhao {
    id: number;
    propriedadeId: number;
    nome: string;
    areaHectares: number;
    ativo: boolean
};

export interface CadastroTalhao {
    nome: string;
    areaHectares: number;
}

export function listarTalhoes(propriedadeId: number,): Promise<Talhao[]> {
    return apiFetch<Talhao[]> (
        `/api/propriedades/${propriedadeId}/talhoes`
    );
}

export function cadastrarTalhao(propriedadeId: number, dados: CadastroTalhao,): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes`, {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function atualizarTalhao(propriedadeId: number, talhaoId: number, dados: CadastroTalhao): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}`, {
            method: "PUT",
            body: JSON.stringify(dados),
        },
    );
}

export function ativarTalhao(propriedadeId: number, talhaoId: number): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}/ativar`, {
            method: "PATCH"
        }
    );
}

export function desativarTalhao(propriedadeId: number, talhaoId: number): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}/desativar`, {
            method: "PATCH"
        }
    )
}

export function buscarTalhaoPorId(propriedadeId: number, talhaoId: number): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}`
    );
}