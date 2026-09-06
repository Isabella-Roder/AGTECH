import { apiFetch } from "./cliente";

export type UnidadeMedida =
    | "KG"
    | "LITRO"
    | "SACA"
    | "UNIDADE";

export type CategoriaProduto =
    | "SEMENTE"
    | "FERTILIZANTE"
    | "DEFENSIVO"
    | "COMBUSTIVEL"
    | "OUTRO";

export interface Produto {
    id: string;
    nome: string;
    unidadeMedida: UnidadeMedida;
    categoria: CategoriaProduto;
    ativo: boolean;
}

export interface CadastroProduto {
    nome: string;
    unidadeMedida: UnidadeMedida;
    categoria: CategoriaProduto;
}

export function listarProdutos(): Promise<Produto[]> {
    return apiFetch<Produto[]> (
        "/api/produtos"
    );
}

export function cadastrarProduto(dados: CadastroProduto): Promise<Produto> {
    return apiFetch<Produto> (
        "/api/produtos", {
            method: "POST",
            body: JSON.stringify(dados)
        }
    );
}

export function desativarProduto(id: string): Promise<Produto> {
    return apiFetch<Produto> (
        `/api/produtos/${id}/desativar`, {
            method: "PATCH"
        }
    );
}

export function ativarProduto(id: string): Promise<Produto> {
    return apiFetch<Produto> (
        `/api/produtos/${id}/ativar`, {
            method: "PATCH"
        }
    );
}
