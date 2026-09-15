package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.TipoMaquina;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastroMaquinaRequest(
        @NotBlank(message = "Identificador é obrigatório")
        @Size(max = 100)
        String identificador,

        @NotNull(message = "Tipo é obrigatório")
        TipoMaquina tipo,

        @NotNull(message = "Horimetro atual é obrigatório")
        @Positive 
        Double horimetroAtual

) {
}
