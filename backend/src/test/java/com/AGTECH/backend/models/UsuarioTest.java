package com.AGTECH.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.AGTECH.backend.exception.RegraDeNegocioException;

class UsuarioTest {

    @Test
    void deveDesativarUsuarioAtivo() {
        Usuario usuario = new Usuario();
        usuario.setAtivo(true);

        usuario.desativar();

        assertEquals(false, usuario.isAtivo());
    }

    @Test
    void deveRecusarDesativarUsuarioDesativado() {
        Usuario usuario = new Usuario();
        usuario.setAtivo(false);

        assertThrows(RegraDeNegocioException.class, () -> usuario.desativar());
    }

    @Test
    void deveAtivarUsuarioDesativado() {
        Usuario usuario = new Usuario();
        usuario.setAtivo(false);

        usuario.ativar();

        assertEquals(true, usuario.isAtivo());
    }

    @Test
    void deveRecusarAtivarUsuarioAtivo() {
        Usuario usuario = new Usuario();
        usuario.setAtivo(true);

        assertThrows(RegraDeNegocioException.class, () -> usuario.ativar());
    }
}
