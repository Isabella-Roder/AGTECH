package com.AGTECH.backend.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastroAbastecimentoRequest(
    @NotNull(message = "Maquina é obrigatória")
    UUID maquinaId,

    @NotNull(message = "Data é obrigatória")
    LocalDateTime data,

    @NotNull(message = "Litros é obrigatório")
    @Positive(message = "Litros deve ser positivo")
    Double litros,

    @NotNull(message = "Horimetro no momento é obrigatório")
    @Positive(message = "Horimetro no momento deve ser positivo")
    Double horimetroNoMomento,

    @Size(
        max = 500,
        message = "Observações deve conter no máximo 500 caracteres"
    )
    String observacoes
) {
    
}
