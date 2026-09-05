package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroCulturaRequest;
import com.AGTECH.backend.dtos.CulturaResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Cultura;
import com.AGTECH.backend.repository.CulturaRepository;

@ExtendWith(MockitoExtension.class)
class CulturaServiceTest {

    @Mock
    private CulturaRepository culturaRepository;

    @InjectMocks
    private CulturaService culturaService;

    @Test
    void deveCadastrarCulturaQuandoNomeNaoExiste() {
        CadastroCulturaRequest request = new CadastroCulturaRequest("Soja");

        when(culturaRepository.existsByNome("Soja")).thenReturn(false);
        when(culturaRepository.save(any(Cultura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CulturaResponse response = culturaService.cadastrar(request);

        assertEquals("Soja", response.nome());
    }

    @Test
    void deveRecusarCadastroDeCulturaJaExistente() {
        CadastroCulturaRequest request = new CadastroCulturaRequest("Soja");

        when(culturaRepository.existsByNome("Soja")).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> culturaService.cadastrar(request));
    }

    @Test
    void deveListarTodasAsCulturas() {
        Cultura soja = new Cultura("Soja");
        Cultura milho = new Cultura("Milho");

        when(culturaRepository.findAll()).thenReturn(List.of(soja, milho));

        List<CulturaResponse> resultado = culturaService.listar();

        assertEquals(2, resultado.size());
    }
}
