package com.AGTECH.backend.dtos;

import com.AGTECH.backend.models.LancamentoFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoFinanceiroResponse(
        UUID id,
        UUID propriedadeId,
        UUID categoriaId,
        BigDecimal valor,
        LocalDate data,
        String descricao,
        UUID safraId,
        boolean ativo
) {
    public static LancamentoFinanceiroResponse from(LancamentoFinanceiro l) {
        return new LancamentoFinanceiroResponse(
                l.getId(),
                l.getPropriedade().getId(),
                l.getCategoria().getId(),
                l.getValor(),
                l.getData(),
                l.getDescricao(),
                l.getSafra() != null ? l.getSafra().getId() : null,
                l.isAtivo()
        );
    }
}
