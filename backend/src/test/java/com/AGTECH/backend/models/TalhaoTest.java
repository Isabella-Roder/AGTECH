package com.AGTECH.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.AGTECH.backend.exception.RegraDeNegocioException;

class TalhaoTest {
    
    @Test 
    void deveDesativarTalhaoAtivado() {
        Talhao talhao = new Talhao();
        talhao.desativar();

        assertEquals(false, talhao.isAtivo());
    }

    @Test 
    void deveRecusarDesativarTalhaoDesativado() {
        Talhao talhao = new Talhao();
        talhao.setAtivo(false);

        assertThrows(RegraDeNegocioException.class, () -> talhao.desativar());
    }

    @Test 
    void deveAtivarTalhaDesativado() {
        Talhao talhao = new Talhao();
        talhao.setAtivo(false);

        talhao.ativar();

        assertEquals(true, talhao.isAtivo());
    }

    @Test 
    void deveRecusarAtivarTalhaoAtivo() {
        Talhao talhao = new Talhao();

        assertThrows(RegraDeNegocioException.class, () -> talhao.ativar());
    }
}
