package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroSafraRequest(
    @NotNull(message = "Cultura é obrigatṕria")
    UUID culturaId,

    @NotBlank(message = "Nome é obrigatório")
    @Size(
        min = 3,
        max = 70,
        message = "Nome deve conter entre 3 e 70 caracteres."
    )
    String nome,

    @NotNull(message = "Data fim prevista é obrigatória")
    LocalDate dataFimPrevista
) {
    
}
