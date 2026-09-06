package com.AGTECH.backend.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoMovimentacao;
import com.AGTECH.backend.models.MovimentacaoEstoque;

public record MovimentacaoEstoqueResponse(
    UUID id,
    UUID produtoId,
    UUID depositoId,
    TipoMovimentacao tipo,
    Double quantidade,
    LocalDate data,
    UUID safraId,
    String observacoes
) {
    public static MovimentacaoEstoqueResponse from(MovimentacaoEstoque m) {
        return new MovimentacaoEstoqueResponse(
            m.getId(),
            m.getProduto().getId(),
            m.getDeposito().getId(),
            m.getTipo(),
            m.getQuantidade(),
            m.getData(),
            m.getSafra() != null ? m.getSafra().getId() : null,
            m.getObservacoes()
        );
    }
}
