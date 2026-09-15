package com.AGTECH.backend.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.models.Abastecimento;

public record AbastecimentoResponse(
    UUID id,
    UUID maquinaId,
    LocalDateTime data,
    Double litros,
    Double horimetroNoMomento,
    String observacoes
) {
    public static AbastecimentoResponse from(Abastecimento abastecimento) {
        return new AbastecimentoResponse(
            abastecimento.getId(),
            abastecimento.getMaquina().getId(),
            abastecimento.getData(),
            abastecimento.getLitros(),
            abastecimento.getHorimetroNoMomento(),
            abastecimento.getObservacoes()
        );
    }   
}
