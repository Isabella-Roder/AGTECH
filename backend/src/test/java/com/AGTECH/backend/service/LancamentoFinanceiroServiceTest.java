package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroLancamentoFinanceiroRequest;
import com.AGTECH.backend.dtos.LancamentoFinanceiroResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.CategoriaFinanceira;
import com.AGTECH.backend.models.LancamentoFinanceiro;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.CategoriaFinanceiraRepository;
import com.AGTECH.backend.repository.LancamentoFinanceiroRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.SafraRepository;

@ExtendWith(MockitoExtension.class)
class LancamentoFinanceiroServiceTest {

    @Mock private LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    @Mock private PropriedadeRuralRepository propriedadeRuralRepository;
    @Mock private CategoriaFinanceiraRepository categoriaFinanceiraRepository;
    @Mock private SafraRepository safraRepository;
    @Mock private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks private LancamentoFinanceiroService lancamentoFinanceiroService;

    private PropriedadeRural criarPropriedade(UUID id) {
        PropriedadeRural p = mock(PropriedadeRural.class);
        lenient().when(p.getId()).thenReturn(id);
        return p;
    }

    private CategoriaFinanceira criarCategoria(UUID categoriaId, UUID propriedadeId, boolean ativa) {
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = mock(CategoriaFinanceira.class);
        lenient().when(categoria.getId()).thenReturn(categoriaId);
        lenient().when(categoria.getPropriedade()).thenReturn(propriedade);
        lenient().when(categoria.isAtivo()).thenReturn(ativa);
        return categoria;
    }

    private Safra criarSafra(UUID safraId, UUID propriedadeId) {
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        Talhao talhao = mock(Talhao.class);
        lenient().when(talhao.getPropriedade()).thenReturn(propriedade);
        Safra safra = mock(Safra.class);
        lenient().when(safra.getId()).thenReturn(safraId);
        lenient().when(safra.getTalhao()).thenReturn(talhao);
        return safra;
    }

    private CadastroLancamentoFinanceiroRequest criarRequest(UUID categoriaId, UUID safraId) {
        return new CadastroLancamentoFinanceiroRequest(
                categoriaId, new BigDecimal("150.75"), LocalDate.of(2026, 3, 10), "Compra de sementes", safraId);
    }

    private LancamentoFinanceiro criarLancamento(UUID propriedadeId, CategoriaFinanceira categoria) {
        return new LancamentoFinanceiro(
                criarPropriedade(propriedadeId), categoria,
                new BigDecimal("10.00"), LocalDate.of(2026, 1, 1), "antigo", null);
    }

    @Test
    void cadastrar_deveSalvarSemSafra() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = criarCategoria(categoriaId, propriedadeId, true);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(categoriaId, null), usuarioId);

        assertEquals(new BigDecimal("150.75"), response.valor());
        assertEquals(categoriaId, response.categoriaId());
        assertEquals(propriedadeId, response.propriedadeId());
        assertNull(response.safraId());
        assertTrue(response.ativo());
        verify(acessoService).verificarAcesso(usuarioId, propriedadeId);
        verifyNoInteractions(safraRepository);
    }

    @Test
    void cadastrar_deveSalvarComSafraDaMesmaPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = criarCategoria(categoriaId, propriedadeId, true);
        Safra safra = criarSafra(safraId, propriedadeId);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(categoriaId, safraId), UUID.randomUUID());

        assertEquals(safraId, response.safraId());
    }

    @Test
    void cadastrar_deveFalharQuandoPropriedadeNaoExiste() {
        UUID propriedadeId = UUID.randomUUID();
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(UUID.randomUUID(), null), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void cadastrar_deveFalharQuandoCategoriaNaoExiste() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.findById(categoriaId)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(categoriaId, null), UUID.randomUUID()));
    }

    @Test
    void cadastrar_deveFalharQuandoCategoriaDeOutraPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = criarCategoria(categoriaId, UUID.randomUUID(), true);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(categoriaId, null), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void cadastrar_deveFalharQuandoCategoriaDesativada() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = criarCategoria(categoriaId, propriedadeId, false);
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(categoriaId, null), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void cadastrar_deveFalharQuandoSafraDeOutraPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        PropriedadeRural propriedade = criarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = criarCategoria(categoriaId, propriedadeId, true);
        Safra safra = criarSafra(safraId, UUID.randomUUID());
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedade));
        when(categoriaFinanceiraRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.cadastrar(
                propriedadeId, criarRequest(categoriaId, safraId), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void atualizar_deveAlterarCamposMantendoCategoria() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(categoriaId, propriedadeId, true);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.atualizar(
                propriedadeId, id, criarRequest(categoriaId, null), UUID.randomUUID());

        assertEquals(new BigDecimal("150.75"), response.valor());
        assertEquals("Compra de sementes", response.descricao());
        assertEquals(categoriaId, response.categoriaId());
        verify(lancamentoFinanceiroRepository).save(existente);
        verifyNoInteractions(categoriaFinanceiraRepository);
    }

    @Test
    void atualizar_deveTrocarParaOutraCategoriaDaMesmaPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaAntigaId = UUID.randomUUID();
        UUID categoriaNovaId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira antiga = criarCategoria(categoriaAntigaId, propriedadeId, true);
        CategoriaFinanceira nova = criarCategoria(categoriaNovaId, propriedadeId, true);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, antiga);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(categoriaFinanceiraRepository.findById(categoriaNovaId)).thenReturn(Optional.of(nova));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.atualizar(
                propriedadeId, id, criarRequest(categoriaNovaId, null), UUID.randomUUID());

        assertEquals(categoriaNovaId, response.categoriaId());
    }

    @Test
    void atualizar_deveFalharAoTrocarParaCategoriaDeOutraPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaAntigaId = UUID.randomUUID();
        UUID categoriaNovaId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira antiga = criarCategoria(categoriaAntigaId, propriedadeId, true);
        CategoriaFinanceira nova = criarCategoria(categoriaNovaId, UUID.randomUUID(), true);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, antiga);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(categoriaFinanceiraRepository.findById(categoriaNovaId)).thenReturn(Optional.of(nova));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.atualizar(
                propriedadeId, id, criarRequest(categoriaNovaId, null), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void atualizar_naoDeveExigirCategoriaAtivaQuandoNaoMudaDeCategoria() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira desativada = criarCategoria(categoriaId, propriedadeId, false);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, desativada);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.atualizar(
                propriedadeId, id, criarRequest(categoriaId, null), UUID.randomUUID());

        assertEquals(categoriaId, response.categoriaId());
    }

    @Test
    void atualizar_deveFalharQuandoSafraDeOutraPropriedade() {
        UUID propriedadeId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(categoriaId, propriedadeId, true);
        Safra safra = criarSafra(safraId, UUID.randomUUID());
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.atualizar(
                propriedadeId, id, criarRequest(categoriaId, safraId), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void atualizar_deveFalharQuandoLancamentoDeOutraPropriedade() {
        UUID categoriaId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(categoriaId, UUID.randomUUID(), true);
        LancamentoFinanceiro existente = criarLancamento(UUID.randomUUID(), categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.atualizar(
                UUID.randomUUID(), id, criarRequest(categoriaId, null), UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void desativar_deveMarcarLancamentoComoInativo() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), propriedadeId, true);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.desativar(
                propriedadeId, id, UUID.randomUUID());

        assertFalse(response.ativo());
    }

    @Test
    void ativar_deveMarcarLancamentoComoAtivo() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), propriedadeId, true);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, categoria);
        existente.desativar();
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));
        when(lancamentoFinanceiroRepository.save(any(LancamentoFinanceiro.class))).thenAnswer(i -> i.getArgument(0));

        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.ativar(
                propriedadeId, id, UUID.randomUUID());

        assertTrue(response.ativo());
    }

    @Test
    void desativar_deveFalharQuandoLancamentoDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), UUID.randomUUID(), true);
        LancamentoFinanceiro existente = criarLancamento(UUID.randomUUID(), categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.desativar(
                UUID.randomUUID(), id, UUID.randomUUID()));
        verify(lancamentoFinanceiroRepository, never()).save(any());
    }

    @Test
    void listarPorPropriedade_deveRetornarLancamentos() {
        UUID propriedadeId = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), propriedadeId, true);
        LancamentoFinanceiro lancamento = criarLancamento(propriedadeId, categoria);
        when(lancamentoFinanceiroRepository.findByPropriedadeId(propriedadeId))
                .thenReturn(List.of(lancamento));

        List<LancamentoFinanceiroResponse> resultado =
                lancamentoFinanceiroService.listarPorPropriedade(propriedadeId, UUID.randomUUID());

        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).safraId());
    }

    @Test
    void buscarPorId_deveRetornarLancamento() {
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), propriedadeId, true);
        LancamentoFinanceiro existente = criarLancamento(propriedadeId, categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));

        LancamentoFinanceiroResponse response =
                lancamentoFinanceiroService.buscarPorId(propriedadeId, id, UUID.randomUUID());

        assertEquals("antigo", response.descricao());
    }

    @Test
    void buscarPorId_deveFalharQuandoLancamentoDeOutraPropriedade() {
        UUID id = UUID.randomUUID();
        CategoriaFinanceira categoria = criarCategoria(UUID.randomUUID(), UUID.randomUUID(), true);
        LancamentoFinanceiro existente = criarLancamento(UUID.randomUUID(), categoria);
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.buscarPorId(
                UUID.randomUUID(), id, UUID.randomUUID()));
    }

    @Test
    void buscarPorId_deveFalharQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(lancamentoFinanceiroRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> lancamentoFinanceiroService.buscarPorId(
                UUID.randomUUID(), id, UUID.randomUUID()));
    }
}
