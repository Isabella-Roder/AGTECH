package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroDepositoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 2,
        max = 100,
        message = "Nome deve conter entre 2 e 100 caracteres"
    )
    String nome
) {
    
}
