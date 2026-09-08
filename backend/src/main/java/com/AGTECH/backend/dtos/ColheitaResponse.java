package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.UnidadeMedida;
import com.AGTECH.backend.models.Colheita;

public record ColheitaResponse(
    UUID id,
    UUID safraId,
    LocalDate dataColheita,
    Double quantidadeColhida,
    UnidadeMedida unidadeMedida,
    String observacoes
) {
    public static ColheitaResponse from(Colheita colheita) {
        return new ColheitaResponse(
            colheita.getId(),
            colheita.getSafra().getId(),
            colheita.getDataColheita(),
            colheita.getQuantidadeColhida(),
            colheita.getUnidadeMedida(),
            colheita.getObservacoes()
        );
    }
}
