package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.enums.EspecieAnimal;
import com.AGTECH.backend.models.Rebanho;

public record RebanhoResponse(
    UUID id,
    UUID propriedadeId,
    String nome,
    EspecieAnimal especie,
    boolean ativo
) {
    public static RebanhoResponse from(Rebanho r) {
        return new RebanhoResponse(
            r.getId(),
            r.getPropriedade().getId(),
            r.getNome(),
            r.getEspecie(),
            r.isAtivo()
        );
    }    
}
