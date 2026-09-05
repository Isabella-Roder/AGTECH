package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.models.Cultura;

public record CulturaResponse(
    UUID id,
    String nome
) {
    public static CulturaResponse from(Cultura cultura) {
        return new CulturaResponse(cultura.getId(), cultura.getNome());
    }
}
