package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.enums.CategoriaProduto;
import com.AGTECH.backend.enums.UnidadeMedida;
import com.AGTECH.backend.models.Produto;

public record ProdutoResponse(
    UUID id,
    String nome,
    UnidadeMedida unidadeMedida,
    CategoriaProduto categoria,
    boolean ativo
) {
    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
            produto.getId(),
            produto.getNome(),
            produto.getUnidadeMedida(),
            produto.getCategoria(),
            produto.isAtivo()
        );
    }
}
