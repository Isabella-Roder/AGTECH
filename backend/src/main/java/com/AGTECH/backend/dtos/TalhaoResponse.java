package com.AGTECH.backend.dtos;

import com.AGTECH.backend.models.Talhao;

public record TalhaoResponse(
    Long id,
    Long propriedadeId,
    String nome,
    Double areaHectares,
    boolean ativo
) {
    public static TalhaoResponse from(Talhao talhao) {
        return new TalhaoResponse(
            talhao.getId(),
            talhao.getPropriedade().getId(),
            talhao.getNome(),
            talhao.getAreaHectares(),
            talhao.isAtivo()
        );
    }
}
