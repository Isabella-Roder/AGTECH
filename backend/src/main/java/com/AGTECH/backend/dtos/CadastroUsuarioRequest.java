package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 3,
        max = 80,
        message = "Nome deve conter entre 3 e 80 caracteres"
    )
    String nome,

    @NotBlank(message = "E-mail é obritatório")
    @Email(message = "E-mail inválido")
    String email,

    @NotBlank(message = "Senha é obrigatório")
    @Size(
        min = 8,
        max = 60,
        message = "Senha deve conter entre 8 e 60 caracteres"
    )
    String senha
) {
    
}
