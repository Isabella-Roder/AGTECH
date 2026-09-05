package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.models.Talhao;

public record TalhaoResponse(
    UUID id,
    UUID propriedadeId,
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
