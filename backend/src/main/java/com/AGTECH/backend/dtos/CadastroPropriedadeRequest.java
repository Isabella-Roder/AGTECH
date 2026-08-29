package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastroPropriedadeRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 3,
        max = 80,
        message = "Nome deve conter entre 3 e 80 caracteres"
    )
    String nome,

    @NotBlank(message = "Município é obrigatório")
    @Size(
        max = 80,
        message = "Município deve conter no máximo 80 caracteres"
    )
    String municipio,

    @NotBlank(message = "Estado é obrigatório")
    @Size(
        max = 80,
        message = "Estado deve conter no máximo 80 caracteres"
    )
    String estado,

    @NotNull(message = "Area total de hectares é obrigatório")
    @Positive(message = "Total de hectares tem que ser positivo")
    Double areaTotalHectares
) {
    
}
