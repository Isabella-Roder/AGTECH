package com.AGTECH.backend.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.AGTECH.backend.exception.RegraDeNegocioException;

class PropriedadeRuralTest {
    
    @Test
    void deveDesativarPropriedadeAtiva() {
        PropriedadeRural propriedade = new PropriedadeRural();
        propriedade.setAtivo(true);

        propriedade.desativar();

        assertEquals(false, propriedade.isAtivo());
    }

    @Test
    void deveRecusarDesativarPropriedadeDesativada() {
        PropriedadeRural propriedade = new PropriedadeRural();
        propriedade.setAtivo(false);

        assertThrows(RegraDeNegocioException.class, () -> propriedade.desativar());
    }

    @Test
    void deveAtivarPropriedadeDesativada() {
        PropriedadeRural propriedade = new PropriedadeRural();
        propriedade.setAtivo(false);

        propriedade.ativar();

        assertEquals(true, propriedade.isAtivo());
    }

    @Test
    void deveRecusarAtivarPropriedadeAtiva() {
        PropriedadeRural propriedade = new PropriedadeRural();
        propriedade.setAtivo(true);

        assertThrows(RegraDeNegocioException.class, () -> propriedade.ativar());
    }
}
