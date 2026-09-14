package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroColheitaRequest;
import com.AGTECH.backend.dtos.ColheitaResponse;
import com.AGTECH.backend.enums.UnidadeMedida;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Colheita;
import com.AGTECH.backend.models.Cultura;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.ColheitaRepository;
import com.AGTECH.backend.repository.SafraRepository;

@ExtendWith(MockitoExtension.class)
class ColheitaServiceTest {

    @Mock
    private ColheitaRepository colheitaRepository;

    @Mock
    private SafraRepository safraRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private ColheitaService colheitaService;

    private Talhao criarTalhaoDaPropriedade(UUID propriedadeId, UUID talhaoId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = mock(Talhao.class);
        lenient().when(talhao.getId()).thenReturn(talhaoId);
        lenient().when(talhao.getPropriedade()).thenReturn(propriedade);
        return talhao;
    }

    @Test
    void deveCadastrarColheitaQuandoSafraPertenceAoTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(colheitaRepository.save(any(Colheita.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroColheitaRequest request = new CadastroColheitaRequest(
            safraId, LocalDate.now(), 1200.0, UnidadeMedida.SACA, "Colheita boa"
        );

        ColheitaResponse response = colheitaService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId);

        assertEquals(1200.0, response.quantidadeColhida());
        assertEquals(UnidadeMedida.SACA, response.unidadeMedida());
    }

    @Test
    void deveRecusarCadastroQuandoSafraDeOutroTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao outroTalhao = criarTalhaoDaPropriedade(propriedadeId, UUID.randomUUID());
        Safra safra = new Safra(outroTalhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        CadastroColheitaRequest request = new CadastroColheitaRequest(
            safraId, LocalDate.now(), 1200.0, UnidadeMedida.SACA, null
        );

        assertThrows(RegraDeNegocioException.class,
            () -> colheitaService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId));
    }

    @Test
    void deveListarColheitasDaSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        Colheita colheita1 = new Colheita(safra, LocalDate.now(), 1000.0, UnidadeMedida.SACA, null);
        Colheita colheita2 = new Colheita(safra, LocalDate.now(), 500.0, UnidadeMedida.SACA, null);

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(colheitaRepository.findBySafraId(safraId)).thenReturn(List.of(colheita1, colheita2));

        List<ColheitaResponse> resultado = colheitaService.listarPorSafra(propriedadeId, talhaoId, safraId, usuarioId);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarColheitaPorIdQuandoPertenceASafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);

        Safra safra = mock(Safra.class);
        when(safra.getId()).thenReturn(safraId);
        lenient().when(safra.getTalhao()).thenReturn(talhao);

        Colheita colheita = new Colheita(safra, LocalDate.now(), 800.0, UnidadeMedida.KG, "Colheita parcial");

        UUID colheitaId = UUID.randomUUID();

        when(colheitaRepository.findById(colheitaId)).thenReturn(Optional.of(colheita));

        ColheitaResponse response = colheitaService.buscarPorId(propriedadeId, talhaoId, safraId, colheitaId, usuarioId);

        assertEquals(800.0, response.quantidadeColhida());
    }

    @Test
    void deveRecusarBuscarColheitaDeOutraSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Safra outraSafra = mock(Safra.class);
        when(outraSafra.getId()).thenReturn(UUID.randomUUID());

        Colheita colheita = new Colheita(outraSafra, LocalDate.now(), 800.0, UnidadeMedida.KG, null);

        UUID colheitaId = UUID.randomUUID();

        when(colheitaRepository.findById(colheitaId)).thenReturn(Optional.of(colheita));

        assertThrows(RegraDeNegocioException.class,
            () -> colheitaService.buscarPorId(propriedadeId, talhaoId, safraId, colheitaId, usuarioId));
    }
}
