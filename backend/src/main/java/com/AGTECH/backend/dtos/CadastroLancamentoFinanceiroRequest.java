package com.AGTECH.backend.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastroLancamentoFinanceiroRequest(
        @NotNull(message = "Categoria é obrigatória.")
        UUID categoriaId,

        @NotNull(message = "Valor é obrigatório.")
        @DecimalMin(value = "0.01", message = "Valor deve ser positivo.")
        BigDecimal valor,

        @NotNull(message = "Data é obrigatória.")
        LocalDate data,

        @Size(
                max = 500,
                message = "Descrição deve conter no máximo 500 caracteres."
        )
        String descricao,

        UUID safraId
) {
}
