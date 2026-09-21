import { apiFetch } from "./cliente";

export type TipoMaquina =
    | "TRATOR"
    | "COLHEITADEIRA"
    | "PULVERIZADOR"
    | "IMPLEMENTO";

export interface Maquina {
    id: string;
    propriedadeId: string;
    identificador: string;
    tipo: TipoMaquina;
    horimetroAtual: number;
    ativo: boolean;
}

export interface CadastroMaquina {
    identificador: string;
    tipo: TipoMaquina;
    horimetroAtual: number;
}

const caminhoMaquinas = (propriedadeId: string) =>
    `/api/propriedades/${propriedadeId}/maquinas`;

export function listarMaquinas(propriedadeId: string): Promise<Maquina[]> {
    return apiFetch<Maquina[]>(caminhoMaquinas(propriedadeId));
}

export function buscarMaquinaPorId(propriedadeId: string, maquinaId: string): Promise<Maquina> {
    return apiFetch<Maquina>(`${caminhoMaquinas(propriedadeId)}/${maquinaId}`);
}

export function cadastrarMaquina(propriedadeId: string, dados: CadastroMaquina): Promise<Maquina> {
    return apiFetch<Maquina>(caminhoMaquinas(propriedadeId), {
        method: "POST",
        body: JSON.stringify(dados)
    });
}

export function ativarMaquina(propriedadeId: string, maquinaId: string): Promise<Maquina> {
    return apiFetch<Maquina>(
        `${caminhoMaquinas(propriedadeId)}/${maquinaId}/ativar`, {
            method: "PATCH"
        }
    );
}

export function desativarMaquina(propriedadeId: string, maquinaId: string): Promise<Maquina> {
    return apiFetch<Maquina>(
        `${caminhoMaquinas(propriedadeId)}/${maquinaId}/desativar`, {
            method: "PATCH"
        }
    );
}

export const rotuloTipoMaquina: Record<TipoMaquina, string> = {
    TRATOR: "Trator",
    COLHEITADEIRA: "Colheitadeira",
    PULVERIZADOR: "Pulverizador",
    IMPLEMENTO: "Implemento"
};