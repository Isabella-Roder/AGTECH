package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoMovimentacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastroMovimentacaoEstoqueRequest(
    @NotNull(message = "Produto é obrigatório")
    UUID produtoId,

    @NotNull(message = "Tipo é obrigatório")
    TipoMovimentacao tipo,

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    Double quantidade,

    @NotNull(message = "Data é obrigatória")
    LocalDate data,

    UUID safraId,

    @Size(
        max = 500,
        message = "Observações deve conter no máximo 500 caracteres"
    )
    String observacoes
) {
    
}
