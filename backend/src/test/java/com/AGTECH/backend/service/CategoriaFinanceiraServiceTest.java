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

import com.AGTECH.backend.dtos.CadastroCategoriaFinanceiraRequest;
import com.AGTECH.backend.dtos.CategoriaFinanceiraResponse;
import com.AGTECH.backend.enums.TipoLancamento;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.CategoriaFinanceira;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.CategoriaFinanceiraRepository;
import com.AGTECH.backend.repository.LancamentoFinanceiroRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaFinanceiraServiceTest {

    @Mock private CategoriaFinanceiraRepository categoriaFinanceiraRepository;
    @Mock private LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    @Mock private PropriedadeRuralRepository propriedadeRuralRepository;
    @Mock private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks private CategoriaFinanceiraService categoriaFinanceiraService;

    private PropriedadeRural criarPropriedade(UUID id) {
        PropriedadeRural p = mock(PropriedadeRural.class);
        lenient().when(p.getId()).thenReturn(id);
        return p;
    }

    private CategoriaFinanceira criarCategoria(UUID propriedadeId, TipoLancamento tipo) {
        return new CategoriaFinanceira(criarPropriedade(propriedadeId), "Sementes", tipo);
    }

    @Test
    void cadastrar_deveSalvarCategoria() {
        UUID propriedadeId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.save(any(CategoriaFinanceira.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaFinanceiraResponse response = categoriaFinanceiraService.cadastrar(
                propriedadeId,
                new CadastroCategoriaFinanceiraRequest("Sementes", TipoLancamento.DESPESA),
                usuarioId);

        assertEquals("Sementes", response.nome());
        assertEquals(TipoLancamento.DESPESA, response.tipo());
        assertEquals(propriedadeId, response.propriedadeId());
        assertTrue(response.ativo());
        verify(acessoService).verificarAcesso(usuarioId, propriedadeId);
    }

    @Test
    void cadastrar_deveFalharQuandoPropriedadeNaoExiste() {
        UUID propriedadeId = UUID.randomUUID();
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> categoriaFinanceiraService.cadastrar(
                propriedadeId,
                new CadastroCategoriaFinanceiraRequest("Sementes", TipoLancamento.DESPESA),
                UUID.randomUUID()));
    }

    @Test
    void atualizar_deveAlterarNomeETipoQuandoSemLancamentos() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(propriedadeId, TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(lancamentoFinanceiroRepository.existsByCategoriaId(id)).thenReturn(false);
        when(categoriaFinanceiraRepository.save(any(CategoriaFinanceira.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaFinanceiraResponse response = categoriaFinanceiraService.atualizar(
                propriedadeId, id,
                new CadastroCategoriaFinanceiraRequest("Venda de soja", TipoLancamento.RECEITA),
                UUID.randomUUID());

        assertEquals("Venda de soja", response.nome());
        assertEquals(TipoLancamento.RECEITA, response.tipo());
    }

    @Test
    void atualizar_deveFalharAoTrocarTipoQuandoJaTemLancamentos() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(propriedadeId, TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(lancamentoFinanceiroRepository.existsByCategoriaId(id)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> categoriaFinanceiraService.atualizar(
                propriedadeId, id,
                new CadastroCategoriaFinanceiraRequest("Sementes", TipoLancamento.RECEITA),
                UUID.randomUUID()));
        verify(categoriaFinanceiraRepository, never()).save(any());
        assertEquals(TipoLancamento.DESPESA, categoria.getTipo());
    }

    @Test
    void atualizar_devePermitirAlterarNomeMantendoTipoMesmoComLancamentos() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(propriedadeId, TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaFinanceiraRepository.save(any(CategoriaFinanceira.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaFinanceiraResponse response = categoriaFinanceiraService.atualizar(
                propriedadeId, id,
                new CadastroCategoriaFinanceiraRequest("Adubos", TipoLancamento.DESPESA),
                UUID.randomUUID());

        assertEquals("Adubos", response.nome());
        verify(lancamentoFinanceiroRepository, never()).existsByCategoriaId(any());
    }

    @Test
    void atualizar_deveFalharQuandoCategoriaDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));

        assertThrows(RegraDeNegocioException.class, () -> categoriaFinanceiraService.atualizar(
                UUID.randomUUID(), id,
                new CadastroCategoriaFinanceiraRequest("Sementes", TipoLancamento.DESPESA),
                UUID.randomUUID()));
        verify(categoriaFinanceiraRepository, never()).save(any());
    }

    @Test
    void desativar_deveMarcarCategoriaComoInativa() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(propriedadeId, TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaFinanceiraRepository.save(any(CategoriaFinanceira.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaFinanceiraResponse response = categoriaFinanceiraService.desativar(
                propriedadeId, id, UUID.randomUUID());

        assertFalse(response.ativo());
    }

    @Test
    void ativar_deveMarcarCategoriaComoAtiva() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(propriedadeId, TipoLancamento.DESPESA);
        categoria.desativar();
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaFinanceiraRepository.save(any(CategoriaFinanceira.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaFinanceiraResponse response = categoriaFinanceiraService.ativar(
                propriedadeId, id, UUID.randomUUID());

        assertTrue(response.ativo());
    }

    @Test
    void desativar_deveFalharQuandoCategoriaDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));

        assertThrows(RegraDeNegocioException.class, () -> categoriaFinanceiraService.desativar(
                UUID.randomUUID(), id, UUID.randomUUID()));
        verify(categoriaFinanceiraRepository, never()).save(any());
    }

    @Test
    void listarPorPropriedade_deveRetornarCategorias() {
        UUID propriedadeId = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(propriedadeId, TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findByPropriedadeId(propriedadeId))
                .thenReturn(List.of(categoria));

        List<CategoriaFinanceiraResponse> resultado =
                categoriaFinanceiraService.listarPorPropriedade(propriedadeId, UUID.randomUUID());

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_deveFalharQuandoCategoriaDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), TipoLancamento.DESPESA);
        when(categoriaFinanceiraRepository.findById(id)).thenReturn(Optional.of(categoria));

        assertThrows(RegraDeNegocioException.class, () -> categoriaFinanceiraService.buscarPorId(
                UUID.randomUUID(), id, UUID.randomUUID()));
    }
}
