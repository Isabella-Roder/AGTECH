import { apiFetch } from "./cliente";

export interface Deposito {
    id: string;
    propriedadeId: string;
    nome: string;
    ativo: boolean;
}

export interface CadastroDeposito {
    nome: string;
}

const caminhoDepositos = (propriedadeId: string) =>
    `/api/propriedades/${propriedadeId}/depositos`;

export function listarDepositos(propriedadeId: string): Promise<Deposito[]> {
    return apiFetch<Deposito[]> (
        caminhoDepositos(propriedadeId)
    );
}

export function cadastrarDeposito(propriedadeId: string, dados: CadastroDeposito): Promise<Deposito> {
    return apiFetch<Deposito> (
        caminhoDepositos(propriedadeId), {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function atualizarDeposito(propriedadeId: string, depositoId: string, dados: CadastroDeposito): Promise<Deposito> {
    return apiFetch<Deposito> (
        `${caminhoDepositos(propriedadeId)}/${depositoId}`, {
            method: "PUT",
            body: JSON.stringify(dados)
        }
    );
}

export function desativarDeposito(propriedadeId: string, depositoId: string): Promise<Deposito> {
    return apiFetch<Deposito> (
        `${caminhoDepositos(propriedadeId)}/${depositoId}/desativar`, {
            method: "PATCH"
        }
    );
}

export function ativarDeposito(propriedadeId: string, depositoId: string): Promise<Deposito> {
    return apiFetch<Deposito> (
        `${caminhoDepositos(propriedadeId)}/${depositoId}/ativar`, {
            method: "PATCH"
        }
    );
}

export function buscarDepositoPorId(propriedadeId: string, depositoId: string): Promise<Deposito> {
    return apiFetch<Deposito> (
        `${caminhoDepositos(propriedadeId)}/${depositoId}`
    );
}
