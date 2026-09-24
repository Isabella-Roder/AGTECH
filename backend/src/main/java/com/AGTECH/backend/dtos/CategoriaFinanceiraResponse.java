package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.TipoLancamento;
import com.AGTECH.backend.models.CategoriaFinanceira;

import java.util.UUID;

public record CategoriaFinanceiraResponse(
        UUID id,
        UUID propriedadeId,
        String nome,
        TipoLancamento tipo,
        boolean ativo
) {
    public static CategoriaFinanceiraResponse from(CategoriaFinanceira c) {
        return new CategoriaFinanceiraResponse(
                c.getId(),
                c.getPropriedade().getId(),
                c.getNome(),
                c.getTipo(),
                c.isAtivo()
        );
    }
}
