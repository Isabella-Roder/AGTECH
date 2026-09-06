package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.CategoriaProduto;
import com.AGTECH.backend.enums.UnidadeMedida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroProdutoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 2,
        max = 100,
        message = "Nome deve conter entre 2 e 100 caracteres"
    )
    String nome,

    @NotNull(message = "Unidade de medida é obrigatória")
    UnidadeMedida unidadeMedida,

    @NotNull(message = "Categoria é obrigatória")
    CategoriaProduto categoria
) {
    
}
