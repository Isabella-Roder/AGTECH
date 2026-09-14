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

import com.AGTECH.backend.dtos.AtividadeDeCampoResponse;
import com.AGTECH.backend.dtos.CadastroAtividadeDeCampoRequest;
import com.AGTECH.backend.enums.TipoAtividade;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.AtividadeDeCampo;
import com.AGTECH.backend.models.Cultura;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.AtividadeDeCampoRepository;
import com.AGTECH.backend.repository.SafraRepository;

@ExtendWith(MockitoExtension.class)
class AtividadeDeCampoServiceTest {

    @Mock
    private AtividadeDeCampoRepository campoRepository;

    @Mock
    private SafraRepository safraRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private AtividadeDeCampoService atividadeDeCampoService;

    private Talhao criarTalhaoDaPropriedade(UUID propriedadeId, UUID talhaoId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = mock(Talhao.class);
        lenient().when(talhao.getId()).thenReturn(talhaoId);
        lenient().when(talhao.getPropriedade()).thenReturn(propriedade);
        return talhao;
    }

    @Test
    void deveCadastrarAtividadeQuandoSafraPertenceAoTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(campoRepository.save(any(AtividadeDeCampo.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroAtividadeDeCampoRequest request = new CadastroAtividadeDeCampoRequest(
            safraId, TipoAtividade.ADUBACAO, LocalDate.now(), "Aplicação de ureia"
        );

        AtividadeDeCampoResponse response = atividadeDeCampoService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId);

        assertEquals(TipoAtividade.ADUBACAO, response.tipo());
        assertEquals("Aplicação de ureia", response.observacoes());
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

        CadastroAtividadeDeCampoRequest request = new CadastroAtividadeDeCampoRequest(
            safraId, TipoAtividade.ADUBACAO, LocalDate.now(), null
        );

        assertThrows(RegraDeNegocioException.class,
            () -> atividadeDeCampoService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId));
    }

    @Test
    void deveListarAtividadesDaSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);
        Safra safra = new Safra(talhao, new Cultura("Soja"), "2025/2026", LocalDate.now().plusMonths(6));

        AtividadeDeCampo atividade1 = new AtividadeDeCampo(safra, LocalDate.now(), TipoAtividade.PLANTIO, null);
        AtividadeDeCampo atividade2 = new AtividadeDeCampo(safra, LocalDate.now(), TipoAtividade.IRRIGACAO, null);

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(campoRepository.findBySafraId(safraId)).thenReturn(List.of(atividade1, atividade2));

        List<AtividadeDeCampoResponse> resultado = atividadeDeCampoService.listarPorSafra(propriedadeId, talhaoId, safraId, usuarioId);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarAtividadePorIdQuandoPertenceASafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Talhao talhao = criarTalhaoDaPropriedade(propriedadeId, talhaoId);

        Safra safra = mock(Safra.class);
        when(safra.getId()).thenReturn(safraId);
        lenient().when(safra.getTalhao()).thenReturn(talhao);

        AtividadeDeCampo atividade = new AtividadeDeCampo(safra, LocalDate.now(), TipoAtividade.CAPINA, "Capina manual");

        UUID atividadeId = UUID.randomUUID();

        when(campoRepository.findById(atividadeId)).thenReturn(Optional.of(atividade));

        AtividadeDeCampoResponse response = atividadeDeCampoService.buscarPorId(propriedadeId, talhaoId, safraId, atividadeId, usuarioId);

        assertEquals(TipoAtividade.CAPINA, response.tipo());
    }

    @Test
    void deveRecusarBuscarAtividadeDeOutraSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Safra outraSafra = mock(Safra.class);
        when(outraSafra.getId()).thenReturn(UUID.randomUUID());

        AtividadeDeCampo atividade = new AtividadeDeCampo(outraSafra, LocalDate.now(), TipoAtividade.PULVERIZACAO, null);

        UUID atividadeId = UUID.randomUUID();

        when(campoRepository.findById(atividadeId)).thenReturn(Optional.of(atividade));

        assertThrows(RegraDeNegocioException.class,
            () -> atividadeDeCampoService.buscarPorId(propriedadeId, talhaoId, safraId, atividadeId, usuarioId));
    }
}
