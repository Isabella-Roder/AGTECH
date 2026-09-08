package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.UnidadeMedida;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroColheitaRequest(

    @NotNull(message = "Safra é obrigatória")
    UUID safraId,

    @NotNull(message = "Data colheita é obrigatória")
    LocalDate dataColheita,

    @NotNull(message = "Quantidade colheita é obrigatória")
    Double quantidadeColhida,

    @NotNull(message = "Unidade de medida é obrigatória")
    UnidadeMedida unidadeMedida,

    @Size(
        max = 500,
        message = "Observações deve conter no máximo 500 caracteres"
    )
    String observacoes
) {
    
}
