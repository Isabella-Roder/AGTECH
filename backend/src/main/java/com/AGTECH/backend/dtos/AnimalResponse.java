package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.SexoAnimal;
import com.AGTECH.backend.models.Animal;

public record AnimalResponse(
    UUID id,
    UUID rebanhoId,
    String identificacao,
    SexoAnimal sexo,
    LocalDate dataNascimento,
    boolean ativo
) {
    public static AnimalResponse from(Animal a) {
        return new AnimalResponse(
            a.getId(),
            a.getRebanho().getId(),
            a.getIdentificacao(),
            a.getSexo(),
            a.getDataNascimento(),
            a.isAtivo()
        );
    }
}
