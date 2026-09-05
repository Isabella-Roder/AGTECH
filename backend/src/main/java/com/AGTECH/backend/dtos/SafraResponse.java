package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.StatusSafra;
import com.AGTECH.backend.models.Safra;

public record SafraResponse(
    UUID id,
    UUID talhaoId,
    UUID culturaId,
    String nome,
    LocalDate dataInicio,
    LocalDate dataFimPrevisto,
    LocalDate dataFimReal,
    StatusSafra status
) {
    public static SafraResponse from(Safra safra) {
        return new SafraResponse(
            safra.getId(),
            safra.getTalhao().getId(),
            safra.getCultura().getId(),
            safra.getNome(),
            safra.getDataInicio(),
            safra.getDataFimPrevista(),
            safra.getDataFimReal(),
            safra.getStatus()
        );
    }
}
