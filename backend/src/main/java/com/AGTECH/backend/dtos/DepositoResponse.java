package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.models.Deposito;

public record DepositoResponse(
    UUID id,
    UUID propriedadeId,
    String nome,
    boolean ativo
) {
    public static DepositoResponse from(Deposito deposito) {
        return new DepositoResponse(
            deposito.getId(),
            deposito.getPropriedade().getId(),
            deposito.getNome(),
            deposito.isAtivo()
        );
    }
}
