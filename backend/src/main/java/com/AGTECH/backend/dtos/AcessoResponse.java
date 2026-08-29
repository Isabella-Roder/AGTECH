package com.AGTECH.backend.dtos;

import com.AGTECH.backend.enums.PapelAcesso;
import com.AGTECH.backend.models.UsuarioPropriedadeAcesso;

public record AcessoResponse(
    Long id,
    Long usuarioId,
    String usuarioNome,
    Long propriedadeId,
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
