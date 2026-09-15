package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.TipoMaquina;
import com.AGTECH.backend.models.Maquina;

import java.util.UUID;

public record MaquinaResponse(
        UUID id,
        UUID propriedadeId,
        String identificador,
        TipoMaquina tipo,
        Double horimetroAtual,
        boolean ativo
) {
    public static MaquinaResponse from(Maquina m) {
        return new MaquinaResponse(
                m.getId(),
                m.getPropriedade().getId(),
                m.getIdentificador(),
                m.getTipo(),
                m.getHorimetroAtual(),
                m.isAtivo()
        );
    }
}
