package com.AGTECH.backend.dtos;

import com.AGTECH.backend.models.PropriedadeRural;

public record PropriedadeResponse(
    Long id,
    String nome,
    String municipio,
    String estado,
    Double areaTotalHectares,
    boolean ativo
) {
    public static PropriedadeResponse from(PropriedadeRural propriedade) {
        return new PropriedadeResponse(
            propriedade.getId(),
            propriedade.getNome(),
            propriedade.getMunicipio(),
            propriedade.getEstado(),
            propriedade.getAreaTotalHectares(),
            propriedade.isAtivo()
        );
    }
}
