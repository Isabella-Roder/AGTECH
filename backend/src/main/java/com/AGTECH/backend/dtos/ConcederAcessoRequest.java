package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.enums.PapelAcesso;

import jakarta.validation.constraints.NotNull;

public record ConcederAcessoRequest(
    @NotNull(message = "Id do usuario é obrigatório")
    UUID usuarioId,

    @NotNull(message = "Papel de acesso é obrigatório")
    PapelAcesso papel
) {
    
}
