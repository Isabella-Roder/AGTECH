package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoAtividade;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroAtividadeDeCampoRequest(
    
    @NotNull(message = "Safra é obrigatório")
    UUID safraId,

    @NotNull(message = "Tipo é obrigatório")
    TipoAtividade tipo,

    @NotNull(message = "Data de realização é obrigatório")
    LocalDate dataRealizacao,

    @Size(
        max = 500,
        message = "Observações deve conter no máximo 500 caracteres."
    )
    String observacoes
) {
    
}
