package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.enums.PapelAcesso;
import com.AGTECH.backend.models.UsuarioPropriedadeAcesso;

public record AcessoResponse(
    UUID id,
    UUID usuarioId,
    String usuarioNome,
    UUID propriedadeId,
    PapelAcesso papel
) {
    public static AcessoResponse from(UsuarioPropriedadeAcesso acesso) {
        return new AcessoResponse(
            acesso.getId(),
            acesso.getUsuario().getId(),
            acesso.getUsuario().getNome(),
            acesso.getPropriedade().getId(),
            acesso.getPapel()
        );
    }
}
