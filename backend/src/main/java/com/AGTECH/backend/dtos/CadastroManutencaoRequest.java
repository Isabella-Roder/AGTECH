package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.TipoManutencao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CadastroManutencaoRequest(
        @NotNull(message = "Maquina é obrigatória")
        UUID maquidaId,

        @NotNull(message = "Tipo é obrigatório")
        TipoManutencao tipo,

        @NotNull(message = "Data de realização é obrigatório")
        LocalDate dataRealizacao,

        @NotNull(message = "Horimetro no momento é obrigatório")
        @Positive(message = "Horimento no momento deve ser positivo")
        Double horimetroNoMomento,

        @Size(
                max = 500,
                message = "Descrição deve conter no máximo 500 caracteres"
        )
        String descricao
) {
}
