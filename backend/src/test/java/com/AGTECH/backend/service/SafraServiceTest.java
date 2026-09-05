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

import com.AGTECH.backend.dtos.CadastroSafraRequest;
import com.AGTECH.backend.dtos.SafraResponse;
import com.AGTECH.backend.enums.StatusSafra;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Cultura;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.CulturaRepository;
import com.AGTECH.backend.repository.SafraRepository;
import com.AGTECH.backend.repository.TalhaoRepository;

@ExtendWith(MockitoExtension.class)
class SafraServiceTest {

    @Mock
    private SafraRepository safraRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private CulturaRepository culturaRepository;

    @InjectMocks
    private SafraService safraService;

    private Talhao criarTalhaoDaPropriedade(UUID propriedadeId, UUID talhaoId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = mock(Talhao.class);
        lenient().when(talhao.getId()).thenReturn(talhaoId);
        lenient().when(talhao.getPropriedade()).thenReturn(propriedade);
        return talhao;
    }

    @Test
    void deveCadastrarSafraQuandoTalhaoPertenceAPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID culturaId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Cultura cultura = new Cultura("Soja");

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(culturaRepository.findById(culturaId)).thenReturn(Optional.of(cultura));
        when(safraRepository.save(any(Safra.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroSafraRequest request = new CadastroSafraRequest(culturaId, "2025/2026", LocalDate.now().plusMonths(6));

        SafraResponse response = safraService.cadastrar(propriedadeId, talhaoId, request, usuarioId);

        assertEquals("2025/2026", response.nome());
        assertEquals(StatusSafra.PLANEJADA, response.status());
    }

    @Test
    void deveRecusarCadastroQuandoTalhaoDeOutraPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID culturaId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(UUID.randomUUID(), talhaoId);

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));

        CadastroSafraRequest request = new CadastroSafraRequest(culturaId, "2025/2026", LocalDate.now().plusMonths(6));

        assertThrows(RegraDeNegocioException.class,
            () -> safraService.cadastrar(propriedadeId, talhaoId, request, usuarioId));
    }

    @Test
    void deveIniciarSafraPlanejada() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(safraRepository.save(safra)).thenReturn(safra);

        SafraResponse response = safraService.iniciar(propriedadeId, talhaoId, safraId, usuarioId);

        assertEquals(StatusSafra.EM_ANDAMENTO, response.status());
    }

    @Test
    void deveRecusarIniciarSafraDeOutroTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Talhao outroTalhao = criarTalhaoDaPropriedade(propriedadeId, UUID.randomUUID());
        Safra safra = new Safra(outroTalhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        assertThrows(RegraDeNegocioException.class,
            () -> safraService.iniciar(propriedadeId, talhaoId, safraId, usuarioId));
    }

    @Test
    void deveFinalizarSafraEmAndamento() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));
        safra.iniciar();

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(safraRepository.save(safra)).thenReturn(safra);

        SafraResponse response = safraService.finalizar(propriedadeId, talhaoId, safraId, usuarioId);

        assertEquals(StatusSafra.FINALIZADA, response.status());
        assertEquals(LocalDate.now(), response.dataFimReal());
    }

    @Test
    void deveRecusarFinalizarSafraPlanejada() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        assertThrows(RegraDeNegocioException.class,
            () -> safraService.finalizar(propriedadeId, talhaoId, safraId, usuarioId));
    }

    @Test
    void deveCancelarSafraPlanejada() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(safraRepository.save(safra)).thenReturn(safra);

        SafraResponse response = safraService.cancelar(propriedadeId, talhaoId, safraId, usuarioId);

        assertEquals(StatusSafra.CANCELADA, response.status());
    }

    @Test
    void deveRecusarCancelarSafraFinalizada() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));
        safra.iniciar();
        safra.finalizar();

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        assertThrows(RegraDeNegocioException.class,
            () -> safraService.cancelar(propriedadeId, talhaoId, safraId, usuarioId));
    }

    @Test
    void deveListarSafrasDoTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra1 = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));
        Safra safra2 = new Safra(talhao, new Cultura("Milho"), "2026/2027", LocalDate.now().plusMonths(12));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findByTalhaoId(talhaoId)).thenReturn(List.of(safra1, safra2));

        List<SafraResponse> resultado = safraService.listar(propriedadeId, talhaoId, usuarioId);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarSafraPorIdQuandoPertenceAoTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        SafraResponse response = safraService.buscarPorId(propriedadeId, talhaoId, safraId, usuarioId);

        assertEquals("2025/2026", response.nome());
    }

    @Test
    void deveRecusarBuscarSafraDeOutroTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Talhao outroTalhao = criarTalhaoDaPropriedade(propriedadeId, UUID.randomUUID());
        Safra safra = new Safra(outroTalhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        assertThrows(RegraDeNegocioException.class,
            () -> safraService.buscarPorId(propriedadeId, talhaoId, safraId, usuarioId));
    }
}
