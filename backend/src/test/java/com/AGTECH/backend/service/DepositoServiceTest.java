package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroDepositoRequest;
import com.AGTECH.backend.dtos.DepositoResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Deposito;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.DepositoRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;

@ExtendWith(MockitoExtension.class)
class DepositoServiceTest {

    @Mock
    private DepositoRepository depositoRepository;

    @Mock
    private PropriedadeRuralRepository propriedadeRuralRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private DepositoService depositoService;

    @Test
    void deveCadastrarDepositoQuandoUsuarioTemAcesso() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(depositoRepository.save(any(Deposito.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroDepositoRequest request = new CadastroDepositoRequest("Galpao 1");

        DepositoResponse response = depositoService.cadastrar(propriedadeId, request, usuarioId);

        assertEquals("Galpao 1", response.nome());
        assertEquals(propriedadeId, response.propriedadeId());
    }

    @Test
    void deveRecusarCadastroQuandoPropriedadeNaoEncontrada() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();

        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.empty());

        CadastroDepositoRequest request = new CadastroDepositoRequest("Galpao 1");

        assertThrows(RegraDeNegocioException.class,
            () -> depositoService.cadastrar(propriedadeId, request, usuarioId));
    }

    @Test
    void deveAtualizarDepositoDaPropriedadeCorreta() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Deposito deposito = new Deposito(propriedade, "Antigo");
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));

        CadastroDepositoRequest request = new CadastroDepositoRequest("Novo Nome");

        DepositoResponse response = depositoService.atualizar(propriedadeId, depositoId, request, usuarioId);

        assertEquals("Novo Nome", response.nome());
    }

    @Test
    void deveRecusarAtualizarDepositoDeOutraPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(UUID.randomUUID());

        Deposito deposito = new Deposito(propriedade, "Antigo");
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));

        CadastroDepositoRequest request = new CadastroDepositoRequest("Novo Nome");

        assertThrows(RegraDeNegocioException.class,
            () -> depositoService.atualizar(propriedadeId, depositoId, request, usuarioId));
    }

    @Test
    void deveDesativarDeposito() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Deposito deposito = new Deposito(propriedade, "Galpao 1");
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(depositoRepository.save(deposito)).thenReturn(deposito);

        DepositoResponse response = depositoService.desativar(propriedadeId, depositoId, usuarioId);

        assertEquals(false, response.ativo());
    }

    @Test
    void deveAtivarDeposito() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Deposito deposito = new Deposito(propriedade, "Galpao 1");
        deposito.desativar();
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(depositoRepository.save(deposito)).thenReturn(deposito);

        DepositoResponse response = depositoService.ativar(propriedadeId, depositoId, usuarioId);

        assertEquals(true, response.ativo());
    }

    @Test
    void deveListarDepositosDaPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Deposito deposito1 = new Deposito(propriedade, "Galpao 1");
        Deposito deposito2 = new Deposito(propriedade, "Silo 1");

        when(depositoRepository.findByPropriedadeId(propriedadeId)).thenReturn(List.of(deposito1, deposito2));

        List<DepositoResponse> resultado = depositoService.listarPorPropriedade(propriedadeId, usuarioId);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarDepositoPorIdQuandoPertenceAPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(propriedadeId);

        Deposito deposito = new Deposito(propriedade, "Galpao 1");
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));

        DepositoResponse response = depositoService.buscarPorId(propriedadeId, depositoId, usuarioId);

        assertEquals("Galpao 1", response.nome());
    }

    @Test
    void deveRecusarBuscarDepositoDeOutraPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        when(propriedade.getId()).thenReturn(UUID.randomUUID());

        Deposito deposito = new Deposito(propriedade, "Galpao 1");
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));

        assertThrows(RegraDeNegocioException.class,
            () -> depositoService.buscarPorId(propriedadeId, depositoId, usuarioId));
    }
}
