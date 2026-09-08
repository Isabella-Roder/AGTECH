package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoAtividade;
import com.AGTECH.backend.models.AtividadeDeCampo;

public record AtividadeDeCampoResponse(
    UUID id,
    UUID safraId,
    TipoAtividade tipo,
    LocalDate dataRealizacao,
    String observacoes
) {
    public static AtividadeDeCampoResponse from(AtividadeDeCampo atividadeDeCampo) {
        return new AtividadeDeCampoResponse(
            atividadeDeCampo.getId(),
            atividadeDeCampo.getSafra().getId(),
            atividadeDeCampo.getTipo(),
            atividadeDeCampo.getDataRealizacao(),
            atividadeDeCampo.getObservacoes()
        );
    }
}
