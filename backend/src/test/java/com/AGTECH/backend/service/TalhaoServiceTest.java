package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroTalhaoRequest;
import com.AGTECH.backend.dtos.TalhaoResponse;
import com.AGTECH.backend.exception.AcessoNegadoException;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.TalhaoRepository;

@ExtendWith(MockitoExtension.class)
class TalhaoServiceTest {

    @Mock
    private TalhaoRepository talhaoRepository;

    @Mock
    private PropriedadeRuralRepository propriedadeRuralRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private TalhaoService talhaoService;

    @Test
    void deveCadastrarTalhaoQuandoUsuarioTemAcesso() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(talhaoRepository.save(any(Talhao.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        when(propriedade.getId()).thenReturn(propriedadeId);

        CadastroTalhaoRequest request = new CadastroTalhaoRequest("Talhão 1", 10.0);

        TalhaoResponse response = talhaoService.cadastrar(propriedadeId, request, usuarioId);

        assertEquals("Talhão 1", response.nome());
        assertEquals(propriedadeId, response.propriedadeId());
        verify(acessoService).verificarAcesso(usuarioId, propriedadeId);
    }

    @Test
    void deveRecusarCadastroQuandoPropriedadeNaoEncontrada() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;

        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.empty());

        CadastroTalhaoRequest request = new CadastroTalhaoRequest("Talhão 1", 10.0);

        assertThrows(RegraDeNegocioException.class,
            () -> talhaoService.cadastrar(propriedadeId, request, usuarioId));
    }

    @Test
    void deveRecusarCadastroQuandoUsuarioNaoTemAcesso() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;

        doThrow(new AcessoNegadoException("Você não tem acesso a essa propriedade."))
            .when(acessoService).verificarAcesso(usuarioId, propriedadeId);

        CadastroTalhaoRequest request = new CadastroTalhaoRequest("Talhão 1", 10.0);

        assertThrows(AcessoNegadoException.class,
            () -> talhaoService.cadastrar(propriedadeId, request, usuarioId));
        verify(propriedadeRuralRepository, never()).findById(any());
    }

    @Test
    void deveAtualizarTalhaoDaPropriedadeCorreta() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;
        Long talhaoId = 3L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = new Talhao(propriedade, "Antigo", 5.0);
        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));

        CadastroTalhaoRequest request = new CadastroTalhaoRequest("Novo Nome", 20.0);

        TalhaoResponse response = talhaoService.atualizar(propriedadeId, talhaoId, request, usuarioId);

        assertEquals("Novo Nome", response.nome());
        assertEquals(20.0, response.areaHectares());
    }

    @Test
    void deveRecusarAtualizarTalhaoDeOutraPropriedade() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;
        Long talhaoId = 3L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(99L);

        Talhao talhao = new Talhao(propriedade, "Antigo", 5.0);
        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));

        CadastroTalhaoRequest request = new CadastroTalhaoRequest("Novo Nome", 20.0);

        assertThrows(RegraDeNegocioException.class,
            () -> talhaoService.atualizar(propriedadeId, talhaoId, request, usuarioId));
    }

    @Test
    void deveDesativarTalhao() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;
        Long talhaoId = 3L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = new Talhao(propriedade, "Talhão", 5.0);
        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(talhaoRepository.save(talhao)).thenReturn(talhao);

        TalhaoResponse response = talhaoService.desativar(propriedadeId, talhaoId, usuarioId);

        assertEquals(false, response.ativo());
    }

    @Test
    void deveAtivarTalhao() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;
        Long talhaoId = 3L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = new Talhao(propriedade, "Talhão", 5.0);
        talhao.desativar();
        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));
        when(talhaoRepository.save(talhao)).thenReturn(talhao);

        TalhaoResponse response = talhaoService.ativar(propriedadeId, talhaoId, usuarioId);

        assertEquals(true, response.ativo());
    }

    @Test
    void deveBuscarTalhaoPorIdQuandoPertenceAPropriedade() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;
        Long talhaoId = 3L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = new Talhao(propriedade, "Talhão", 5.0);
        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));

        TalhaoResponse response = talhaoService.buscarPorId(propriedadeId, talhaoId, usuarioId);

        assertEquals("Talhão", response.nome());
        verify(acessoService).verificarAcesso(usuarioId, propriedadeId);
    }

    @Test
    void deveRecusarBuscarTalhaoDeOutraPropriedade() {
        Long usuarioId = 1L;
        Long propriedadeId = 2L;
        Long talhaoId = 3L;

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(99L);

        Talhao talhao = new Talhao(propriedade, "Talhão", 5.0);
        when(talhaoRepository.findById(talhaoId)).thenReturn(Optional.of(talhao));

        assertThrows(RegraDeNegocioException.class,
            () -> talhaoService.buscarPorId(propriedadeId, talhaoId, usuarioId));
    }
}
