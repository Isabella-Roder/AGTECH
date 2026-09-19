package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.AbastecimentoResponse;
import com.AGTECH.backend.dtos.CadastroAbastecimentoRequest;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Abastecimento;
import com.AGTECH.backend.models.Maquina;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.AbastecimentoRepository;
import com.AGTECH.backend.repository.MaquinaRepository;

@ExtendWith(MockitoExtension.class)
class AbastecimentoServiceTest {

    @Mock private AbastecimentoRepository abastecimentoRepository;
    @Mock private MaquinaRepository maquinaRepository;
    @Mock private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks private AbastecimentoService abastecimentoService;

    private Maquina criarMaquina(UUID maquinaId, UUID propriedadeId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);
        Maquina maquina = mock(Maquina.class);
        lenient().when(maquina.getId()).thenReturn(maquinaId);
        lenient().when(maquina.getPropriedade()).thenReturn(propriedade);
        return maquina;
    }

    private CadastroAbastecimentoRequest criarRequest(UUID maquinaId) {
        return new CadastroAbastecimentoRequest(maquinaId, LocalDateTime.now(), 50.5, 120.0, "obs");
    }

    @Test
    void cadastrar_deveSalvarAbastecimento() {
        UUID maquinaId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        Maquina maquina = criarMaquina(maquinaId, propriedadeId);
        when(maquinaRepository.findById(maquinaId))
                .thenReturn(Optional.of(maquina));
        when(abastecimentoRepository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        AbastecimentoResponse response = abastecimentoService.cadastrar(
                propriedadeId, maquinaId, criarRequest(maquinaId), UUID.randomUUID());

        assertEquals(50.5, response.litros());
        assertEquals(maquinaId, response.maquinaId());
    }

    @Test
    void cadastrar_deveFalharQuandoMaquinaDeOutraPropriedade() {
        UUID maquinaId = UUID.randomUUID();
        Maquina maquina = criarMaquina(maquinaId, UUID.randomUUID());
        when(maquinaRepository.findById(maquinaId))
                .thenReturn(Optional.of(maquina));

        assertThrows(RegraDeNegocioException.class, () -> abastecimentoService.cadastrar(
                UUID.randomUUID(), maquinaId, criarRequest(maquinaId), UUID.randomUUID()));
        verify(abastecimentoRepository, never()).save(any());
    }

    @Test
    void atualizar_deveAlterarCamposESalvar() {
        UUID maquinaId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Abastecimento existente = new Abastecimento(
                criarMaquina(maquinaId, propriedadeId), LocalDateTime.now(), 10.0, 100.0, null);
        when(abastecimentoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(abastecimentoRepository.save(any(Abastecimento.class))).thenAnswer(i -> i.getArgument(0));

        AbastecimentoResponse response = abastecimentoService.atualizar(
                propriedadeId, maquinaId, id, criarRequest(maquinaId), UUID.randomUUID());

        assertEquals(50.5, response.litros());
        assertEquals(120.0, response.horimetroNoMomento());
        verify(abastecimentoRepository).save(existente);
    }

    @Test
    void atualizar_deveFalharQuandoAbastecimentoDeOutraMaquina() {
        UUID id = UUID.randomUUID();
        Abastecimento existente = new Abastecimento(
                criarMaquina(UUID.randomUUID(), UUID.randomUUID()), LocalDateTime.now(), 10.0, 100.0, null);
        when(abastecimentoRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> abastecimentoService.atualizar(
                UUID.randomUUID(), UUID.randomUUID(), id, criarRequest(UUID.randomUUID()), UUID.randomUUID()));
    }

    @Test
    void listarPorMaquina_deveRetornarAbastecimentos() {
        UUID maquinaId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        Maquina maquina = criarMaquina(maquinaId, propriedadeId);
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));
        when(abastecimentoRepository.findByMaquinaId(maquinaId)).thenReturn(
                List.of(new Abastecimento(maquina, LocalDateTime.now(), 10.0, 100.0, null)));

        assertEquals(1, abastecimentoService.listarPorMaquina(propriedadeId, maquinaId, UUID.randomUUID()).size());
    }

    @Test
    void buscarPorId_deveFalharQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(abastecimentoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> abastecimentoService.buscarPorId(
                UUID.randomUUID(), UUID.randomUUID(), id, UUID.randomUUID()));
    }
}
