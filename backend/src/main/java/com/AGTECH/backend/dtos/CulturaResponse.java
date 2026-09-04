package com.AGTECH.backend.dtos;

import com.AGTECH.backend.models.Cultura;

public record CulturaResponse(
    Long id,
    String nome
) {
    public static CulturaResponse from(Cultura cultura) {
        return new CulturaResponse(cultura.getId(), cultura.getNome());
    }
}
