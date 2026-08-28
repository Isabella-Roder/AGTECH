package com.AGTECH.backend.dtos;

import com.AGTECH.backend.models.Usuario;

public record UsuarioResponse(
    Long id,
    String nome,
    String email, 
    boolean ativo
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.isAtivo()
        );
    }
}
