package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroCulturaRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 2,
        max = 60,
        message = "Nome deve conter entre 2 a 60 caracteres"
    )
    String nome
) {
    
}
