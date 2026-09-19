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

import com.AGTECH.backend.dtos.CadastroMaquinaRequest;
import com.AGTECH.backend.dtos.MaquinaResponse;
import com.AGTECH.backend.enums.TipoMaquina;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Maquina;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.MaquinaRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;

@ExtendWith(MockitoExtension.class)
class MaquinaServiceTest {

    @Mock private MaquinaRepository maquinaRepository;
    @Mock private PropriedadeRuralRepository propriedadeRuralRepository;
    @Mock private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks private MaquinaService maquinaService;

    private PropriedadeRural criarPropriedade(UUID id) {
        PropriedadeRural p = mock(PropriedadeRural.class);
        lenient().when(p.getId()).thenReturn(id);
        return p;
    }

    @Test
    void cadastrar_deveSalvarMaquina() {
        UUID propriedadeId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        when(propriedadeRuralRepository.findById(propriedadeId))
                .thenReturn(Optional.of(propriedade));
        when(maquinaRepository.save(any(Maquina.class))).thenAnswer(i -> i.getArgument(0));

        MaquinaResponse response = maquinaService.cadastrar(
                propriedadeId,
                new CadastroMaquinaRequest("Trator 01", TipoMaquina.TRATOR, 100.0),
                usuarioId);

        assertEquals("Trator 01", response.identificador());
        assertEquals(TipoMaquina.TRATOR, response.tipo());
        assertEquals(propriedadeId, response.propriedadeId());
        verify(acessoService).verificarAcesso(usuarioId, propriedadeId);
    }

    @Test
    void cadastrar_deveFalharQuandoPropriedadeNaoExiste() {
        UUID propriedadeId = UUID.randomUUID();
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> maquinaService.cadastrar(
                propriedadeId,
                new CadastroMaquinaRequest("Trator 01", TipoMaquina.TRATOR, 100.0),
                UUID.randomUUID()));
    }

    @Test
    void desativar_deveMarcarMaquinaComoInativa() {
        UUID propriedadeId = UUID.randomUUID();
        UUID maquinaId = UUID.randomUUID();
        Maquina maquina = new Maquina(criarPropriedade(propriedadeId), "Trator 01", TipoMaquina.TRATOR, 100.0);
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));
        when(maquinaRepository.save(any(Maquina.class))).thenAnswer(i -> i.getArgument(0));

        MaquinaResponse response = maquinaService.desativar(propriedadeId, maquinaId, UUID.randomUUID());

        assertFalse(response.ativo());
    }

    @Test
    void ativar_deveMarcarMaquinaComoAtiva() {
        UUID propriedadeId = UUID.randomUUID();
        UUID maquinaId = UUID.randomUUID();
        Maquina maquina = new Maquina(criarPropriedade(propriedadeId), "Trator 01", TipoMaquina.TRATOR, 100.0);
        maquina.desativar();
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));
        when(maquinaRepository.save(any(Maquina.class))).thenAnswer(i -> i.getArgument(0));

        MaquinaResponse response = maquinaService.ativar(propriedadeId, maquinaId, UUID.randomUUID());

        assertTrue(response.ativo());
    }

    @Test
    void desativar_deveFalharQuandoMaquinaDeOutraPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID maquinaId = UUID.randomUUID();
        Maquina maquina = new Maquina(criarPropriedade(UUID.randomUUID()), "Trator 01", TipoMaquina.TRATOR, 100.0);
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));

        assertThrows(RegraDeNegocioException.class,
                () -> maquinaService.desativar(propriedadeId, maquinaId, UUID.randomUUID()));
        verify(maquinaRepository, never()).save(any());
    }

    @Test
    void listarPorPropriedade_deveRetornarMaquinas() {
        UUID propriedadeId = UUID.randomUUID();
        Maquina maquina = new Maquina(criarPropriedade(propriedadeId), "Trator 01", TipoMaquina.TRATOR, 100.0);
        when(maquinaRepository.findByPropriedadeId(propriedadeId)).thenReturn(List.of(maquina));

        List<MaquinaResponse> resultado = maquinaService.listarPorPropriedade(propriedadeId, UUID.randomUUID());

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_deveFalharQuandoMaquinaDeOutraPropriedade() {
        UUID maquinaId = UUID.randomUUID();
        Maquina maquina = new Maquina(criarPropriedade(UUID.randomUUID()), "Trator 01", TipoMaquina.TRATOR, 100.0);
        when(maquinaRepository.findById(maquinaId)).thenReturn(Optional.of(maquina));

        assertThrows(RegraDeNegocioException.class,
                () -> maquinaService.buscarPorId(UUID.randomUUID(), maquinaId, UUID.randomUUID()));
    }
}
