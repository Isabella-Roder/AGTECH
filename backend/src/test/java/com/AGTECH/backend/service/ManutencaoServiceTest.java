package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

import com.AGTECH.backend.dtos.CadastroManutencaoRequest;
import com.AGTECH.backend.dtos.ManutencaoResponse;
import com.AGTECH.backend.enums.TipoManutencao;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Manutencao;
import com.AGTECH.backend.models.Maquina;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.ManutencaoRepository;
import com.AGTECH.backend.repository.MaquinaRepository;

@ExtendWith(MockitoExtension.class)
class ManutencaoServiceTest {

    @Mock
    private ManutencaoRepository manutencaoRepository;

    @Mock
    private MaquinaRepository maquinaRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private ManutencaoService manutencaoService;

    private Maquina criarMaquina(UUID maquinaId, UUID propriedadeId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);
        Maquina maquina = mock(Maquina.class);
        lenient().when(maquina.getId()).thenReturn(maquinaId);
        lenient().when(maquina.getPropriedade()).thenReturn(propriedade);
        return maquina;
    }

    private CadastroManutencaoRequest criarRequest() {
        return new CadastroManutencaoRequest(TipoManutencao.CORRETIVA, LocalDate.now(), 120.0, "nova desc");
    }

    @Test
    void cadastrar_deveSalvarManutencao() {
        UUID maquinaId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        Maquina maquina = criarMaquina(maquinaId, propriedadeId);
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));
        when(manutencaoRepository.save(any(Manutencao.class))).thenAnswer(i -> i.getArgument(0));

        ManutencaoResponse response = manutencaoService.cadastrar(
                propriedadeId, maquinaId, criarRequest(), UUID.randomUUID());

        assertEquals(TipoManutencao.CORRETIVA, response.tipo());
        assertEquals("nova desc", response.descricao());
        assertEquals(maquinaId, response.maquinaId());
    }

    @Test
    void cadastrar_deveFalharQuandoMaquinaDeOutraPropriedade() {
        UUID maquinaId = UUID.randomUUID();
        Maquina maquina = criarMaquina(maquinaId, UUID.randomUUID());
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));

        assertThrows(RegraDeNegocioException.class, () -> manutencaoService.cadastrar(
                UUID.randomUUID(), maquinaId, criarRequest(), UUID.randomUUID()));
        verify(manutencaoRepository, never()).save(any());
    }

    @Test
    void atualizar_deveAlterarCamposESalvar() {
        UUID maquinaId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Manutencao existente = new Manutencao(
                criarMaquina(maquinaId, propriedadeId), TipoManutencao.PREVENTIVA, LocalDate.now(), 100.0, "desc");
        when(manutencaoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(manutencaoRepository.save(any(Manutencao.class))).thenAnswer(i -> i.getArgument(0));

        ManutencaoResponse response = manutencaoService.atualizar(
                propriedadeId, maquinaId, id, criarRequest(), UUID.randomUUID());

        assertEquals(TipoManutencao.CORRETIVA, response.tipo());
        assertEquals(120.0, response.horimetroNoMomento());
        verify(manutencaoRepository).save(existente);
    }

    @Test
    void atualizar_deveFalharQuandoManutencaoDeOutraMaquina() {
        UUID id = UUID.randomUUID();
        Manutencao existente = new Manutencao(
                criarMaquina(UUID.randomUUID(), UUID.randomUUID()),
                TipoManutencao.PREVENTIVA, LocalDate.now(), 100.0, "desc");
        when(manutencaoRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> manutencaoService.atualizar(
                UUID.randomUUID(), UUID.randomUUID(), id, criarRequest(), UUID.randomUUID()));
        verify(manutencaoRepository, never()).save(any());
    }

    @Test
    void listarPorMaquina_deveRetornarManutencoes() {
        UUID maquinaId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        Maquina maquina = criarMaquina(maquinaId, propriedadeId);
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));
        when(manutencaoRepository.findByMaquinaId(maquinaId)).thenReturn(
                List.of(new Manutencao(maquina, TipoManutencao.PREVENTIVA, LocalDate.now(), 100.0, "desc")));

        assertEquals(1, manutencaoService.listarPorMaquina(propriedadeId, maquinaId, UUID.randomUUID()).size());
    }

    @Test
    void buscarPorId_deveFalharQuandoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(manutencaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> manutencaoService.buscarPorId(
                UUID.randomUUID(), UUID.randomUUID(), id, UUID.randomUUID()));
    }
}
