import { apiFetch } from "./cliente";

export interface Talhao {
    id: string;
    propriedadeId: string;
    nome: string;
    areaHectares: number;
    ativo: boolean
};

export interface CadastroTalhao {
    nome: string;
    areaHectares: number;
}

export function listarTalhoes(propriedadeId: string): Promise<Talhao[]> {
    return apiFetch<Talhao[]> (
        `/api/propriedades/${propriedadeId}/talhoes`
    );
}

export function cadastrarTalhao(propriedadeId: string, dados: CadastroTalhao): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes`, {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function atualizarTalhao(propriedadeId: string, talhaoId: string, dados: CadastroTalhao): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}`, {
            method: "PUT",
            body: JSON.stringify(dados),
        },
    );
}

export function ativarTalhao(propriedadeId: string, talhaoId: string): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}/ativar`, {
            method: "PATCH"
        }
    );
}

export function desativarTalhao(propriedadeId: string, talhaoId: string): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}/desativar`, {
            method: "PATCH"
        }
    )
}

export function buscarTalhaoPorId(propriedadeId: string, talhaoId: string): Promise<Talhao> {
    return apiFetch<Talhao> (
        `/api/propriedades/${propriedadeId}/talhoes/${talhaoId}`
    );
}
