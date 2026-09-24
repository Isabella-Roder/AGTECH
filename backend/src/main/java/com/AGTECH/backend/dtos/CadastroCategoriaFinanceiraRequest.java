package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroCategoriaFinanceiraRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(
                max = 120,
                message = "Nome deve conter no máximo 120 caracteres."
        )
        String nome,

        @NotNull(message = "Tipo é obrigatório")
        TipoLancamento tipo
) {
}
