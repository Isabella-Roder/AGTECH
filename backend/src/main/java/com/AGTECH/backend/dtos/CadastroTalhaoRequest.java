package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastroTalhaoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 3,
        max = 80,
        message = "Nome deve conter entre 3 e 80 caracteres"
    )
    String nome,

    @NotNull(message = "Área em hectares é obrigatória")
    @Positive(message = "Área em hectares tem que ser positiva")
    Double areaHectares

) {
    
}
