package com.AGTECH.backend.dtos;

import java.util.UUID;

import com.AGTECH.backend.models.Usuario;

public record UsuarioResponse(
    UUID id,
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
