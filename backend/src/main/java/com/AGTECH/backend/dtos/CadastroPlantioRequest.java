package com.AGTECH.backend.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastroPlantioRequest(
    @NotNull(message = "Data de plantio é obrigatória")
    LocalDate dataPlantio,

    @NotNull(message = "Área plantada é obrigatória")
    @Positive(message = "Área plantada tem que ser positiva")
    Double areaPlantadaHectares,

    @Size (
        max = 500,
        message = "Observações devem ter no máximo 500 caracteres."
    )
    String observacoes
) {
    
}
