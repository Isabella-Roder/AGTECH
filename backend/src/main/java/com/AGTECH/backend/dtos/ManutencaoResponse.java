package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.TipoManutencao;
import com.AGTECH.backend.models.Manutencao;

import java.time.LocalDate;
import java.util.UUID;

public record ManutencaoResponse(
        UUID id,
        UUID maquinaId,
        TipoManutencao tipo,
        LocalDate dataRealizacao,
        Double horimetroNoMomento,
        String descricao
) {
    public static ManutencaoResponse from(Manutencao manutencao) {
        return new ManutencaoResponse(
                manutencao.getId(),
                manutencao.getMaquina().getId(),
                manutencao.getTipo(),
                manutencao.getDataRealizacao(),
                manutencao.getHorimetroNoMomento(),
                manutencao.getDescricao()
        );
    }
}
