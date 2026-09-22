package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroRebanhoRequest;
import com.AGTECH.backend.dtos.RebanhoResponse;
import com.AGTECH.backend.enums.EspecieAnimal;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Rebanho;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.RebanhoRepository;

@ExtendWith(MockitoExtension.class)
class RebanhoServiceTest {

    @Mock private RebanhoRepository rebanhoRepository;
    @Mock private PropriedadeRuralRepository propriedadeRuralRepository;
    @Mock private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks private RebanhoService rebanhoService;

    private PropriedadeRural criarPropriedade(UUID id) {
        PropriedadeRural p = mock(PropriedadeRural.class);
        lenient().when(p.getId()).thenReturn(id);
        return p;
    }

    private CadastroRebanhoRequest criarRequest() {
        return new CadastroRebanhoRequest("Gado de corte", EspecieAnimal.BOVINO);
    }

    @Test
    void cadastrar_deveSalvarRebanho() {
        UUID propriedadeId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        when(propriedadeRuralRepository.findById(propriedadeId))
                .thenReturn(Optional.of(propriedade));
        when(rebanhoRepository.save(any(Rebanho.class))).thenAnswer(i -> i.getArgument(0));

        RebanhoResponse response = rebanhoService.cadastrar(propriedadeId, criarRequest(), usuarioId);

        assertEquals("Gado de corte", response.nome());
        assertEquals(EspecieAnimal.BOVINO, response.especie());
        assertEquals(propriedadeId, response.propriedadeId());
        verify(acessoService).verificarAcesso(usuarioId, propriedadeId);
    }

    @Test
    void cadastrar_deveFalharQuandoPropriedadeNaoExiste() {
        UUID propriedadeId = UUID.randomUUID();
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class,
                () -> rebanhoService.cadastrar(propriedadeId, criarRequest(), UUID.randomUUID()));
    }

    @Test
    void atualizar_deveAlterarCamposESalvar() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Rebanho existente = new Rebanho(criarPropriedade(propriedadeId), "Antigo", EspecieAnimal.OVINO);
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(rebanhoRepository.save(any(Rebanho.class))).thenAnswer(i -> i.getArgument(0));

        RebanhoResponse response = rebanhoService.atualizar(
                propriedadeId, id, criarRequest(), UUID.randomUUID());

        assertEquals("Gado de corte", response.nome());
        assertEquals(EspecieAnimal.BOVINO, response.especie());
        verify(rebanhoRepository).save(existente);
    }

    @Test
    void atualizar_deveFalharQuandoRebanhoDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        Rebanho existente = new Rebanho(criarPropriedade(UUID.randomUUID()), "Antigo", EspecieAnimal.OVINO);
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> rebanhoService.atualizar(
                UUID.randomUUID(), id, criarRequest(), UUID.randomUUID()));
        verify(rebanhoRepository, never()).save(any());
    }

    @Test
    void desativar_deveMarcarRebanhoComoInativo() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Rebanho rebanho = new Rebanho(criarPropriedade(propriedadeId), "Gado", EspecieAnimal.BOVINO);
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(rebanho));
        when(rebanhoRepository.save(any(Rebanho.class))).thenAnswer(i -> i.getArgument(0));

        RebanhoResponse response = rebanhoService.desativar(propriedadeId, id, UUID.randomUUID());

        assertFalse(response.ativo());
    }

    @Test
    void ativar_deveMarcarRebanhoComoAtivo() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Rebanho rebanho = new Rebanho(criarPropriedade(propriedadeId), "Gado", EspecieAnimal.BOVINO);
        rebanho.desativar();
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(rebanho));
        when(rebanhoRepository.save(any(Rebanho.class))).thenAnswer(i -> i.getArgument(0));

        RebanhoResponse response = rebanhoService.ativar(propriedadeId, id, UUID.randomUUID());

        assertTrue(response.ativo());
    }

    @Test
    void desativar_deveFalharQuandoRebanhoDeOutraPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Rebanho rebanho = new Rebanho(criarPropriedade(UUID.randomUUID()), "Gado", EspecieAnimal.BOVINO);
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(rebanho));

        assertThrows(RegraDeNegocioException.class,
                () -> rebanhoService.desativar(propriedadeId, id, UUID.randomUUID()));
        verify(rebanhoRepository, never()).save(any());
    }

    @Test
    void listarPorPropriedade_deveRetornarRebanhos() {
        UUID propriedadeId = UUID.randomUUID();
        Rebanho rebanho = new Rebanho(criarPropriedade(propriedadeId), "Gado", EspecieAnimal.BOVINO);
        when(rebanhoRepository.findByPropriedadeId(propriedadeId)).thenReturn(List.of(rebanho));

        List<RebanhoResponse> resultado = rebanhoService.listarPorPropriedade(propriedadeId, UUID.randomUUID());

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_deveRetornarRebanho() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Rebanho rebanho = new Rebanho(criarPropriedade(propriedadeId), "Gado", EspecieAnimal.BOVINO);
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(rebanho));

        RebanhoResponse response = rebanhoService.buscarPorId(propriedadeId, id, UUID.randomUUID());

        assertEquals("Gado", response.nome());
    }

    @Test
    void buscarPorId_deveFalharQuandoRebanhoDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        Rebanho rebanho = new Rebanho(criarPropriedade(UUID.randomUUID()), "Gado", EspecieAnimal.BOVINO);
        when(rebanhoRepository.findById(id)).thenReturn(Optional.of(rebanho));

        assertThrows(RegraDeNegocioException.class,
                () -> rebanhoService.buscarPorId(UUID.randomUUID(), id, UUID.randomUUID()));
    }
}
