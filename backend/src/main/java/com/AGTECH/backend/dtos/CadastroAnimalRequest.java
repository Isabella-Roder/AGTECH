package com.AGTECH.backend.dtos;

import java.time.LocalDate;

import com.AGTECH.backend.enums.SexoAnimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record CadastroAnimalRequest(
    @NotBlank(message = "Identificação é obrigatória")
    @Size(
        max = 50,
        message = "Identificação deve conter no máximo 50 caracteres."
    )
    String identificacao,

    @NotNull(message = "Sexo é obrigatório")
    SexoAnimal sexo,

    @Past(message = "Data de nascimento deve estar no passado")
    LocalDate dataNascimento
) {
    
}
