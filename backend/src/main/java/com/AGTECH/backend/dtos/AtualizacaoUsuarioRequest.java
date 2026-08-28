package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizacaoUsuarioRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 3,
        max = 80,
        message = "Nome deve conter entre 3 e 80 caracteres"
    )
    String nome,

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String email
) {
    
}
