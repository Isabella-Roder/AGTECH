package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.EspecieAnimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroRebanhoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        max = 100,
        message = "Nome deve conter no máximo 100 caracteres."
    )
    String nome,

    @NotNull(message = "Espécie é obrigatória")
    EspecieAnimal especie
) {
    
}
