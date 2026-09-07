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

import com.AGTECH.backend.dtos.CadastroMovimentacaoEstoqueRequest;
import com.AGTECH.backend.dtos.MovimentacaoEstoqueResponse;
import com.AGTECH.backend.enums.CategoriaProduto;
import com.AGTECH.backend.enums.TipoMovimentacao;
import com.AGTECH.backend.enums.UnidadeMedida;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Deposito;
import com.AGTECH.backend.models.MovimentacaoEstoque;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Produto;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.DepositoRepository;
import com.AGTECH.backend.repository.MovimentacaoEstoqueRepository;
import com.AGTECH.backend.repository.ProdutoRepository;
import com.AGTECH.backend.repository.SafraRepository;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Mock
    private DepositoRepository depositoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private SafraRepository safraRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    private Deposito criarDeposito(UUID propriedadeId, UUID depositoId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);

        Deposito deposito = mock(Deposito.class);
        lenient().when(deposito.getId()).thenReturn(depositoId);
        lenient().when(deposito.getPropriedade()).thenReturn(propriedade);
        return deposito;
    }

    private Produto criarProduto() {
        return new Produto("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);
    }

    @Test
    void deveCadastrarMovimentacaoQuandoUsuarioTemAcesso() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Deposito deposito = criarDeposito(propriedadeId, depositoId);
        Produto produto = criarProduto();

        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(movimentacaoEstoqueRepository.save(any(MovimentacaoEstoque.class)))
            .thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroMovimentacaoEstoqueRequest request = new CadastroMovimentacaoEstoqueRequest(
            produtoId, TipoMovimentacao.ENTRADA, 10.0, LocalDate.now(), null, "Compra"
        );

        MovimentacaoEstoqueResponse response = movimentacaoEstoqueService.cadastrar(propriedadeId, depositoId, request, usuarioId);

        assertEquals(TipoMovimentacao.ENTRADA, response.tipo());
        assertEquals(10.0, response.quantidade());
        assertEquals("Compra", response.observacoes());
    }

    @Test
    void deveRecusarCadastroQuandoDepositoNaoPertenceAPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Deposito deposito = criarDeposito(UUID.randomUUID(), depositoId);
        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));

        CadastroMovimentacaoEstoqueRequest request = new CadastroMovimentacaoEstoqueRequest(
            produtoId, TipoMovimentacao.ENTRADA, 10.0, LocalDate.now(), null, null
        );

        assertThrows(RegraDeNegocioException.class,
            () -> movimentacaoEstoqueService.cadastrar(propriedadeId, depositoId, request, usuarioId));
    }

    @Test
    void deveRecusarCadastroQuandoSafraNaoPertenceAPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Deposito deposito = criarDeposito(propriedadeId, depositoId);
        Produto produto = criarProduto();

        PropriedadeRural outraPropriedade = mock(PropriedadeRural.class);
        when(outraPropriedade.getId()).thenReturn(UUID.randomUUID());

        Talhao talhao = mock(Talhao.class);
        when(talhao.getPropriedade()).thenReturn(outraPropriedade);

        Safra safra = mock(Safra.class);
        when(safra.getTalhao()).thenReturn(talhao);

        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        CadastroMovimentacaoEstoqueRequest request = new CadastroMovimentacaoEstoqueRequest(
            produtoId, TipoMovimentacao.SAIDA, 5.0, LocalDate.now(), safraId, null
        );

        assertThrows(RegraDeNegocioException.class,
            () -> movimentacaoEstoqueService.cadastrar(propriedadeId, depositoId, request, usuarioId));
    }

    @Test
    void deveListarMovimentacoesDoDeposito() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        Deposito deposito = criarDeposito(propriedadeId, depositoId);
        Produto produto = criarProduto();

        MovimentacaoEstoque movimentacao1 = new MovimentacaoEstoque(
            produto, deposito, TipoMovimentacao.ENTRADA, 10.0, LocalDate.now(), null, null
        );
        MovimentacaoEstoque movimentacao2 = new MovimentacaoEstoque(
            produto, deposito, TipoMovimentacao.SAIDA, 5.0, LocalDate.now(), null, null
        );

        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(movimentacaoEstoqueRepository.findByDepositoId(depositoId)).thenReturn(List.of(movimentacao1, movimentacao2));

        List<MovimentacaoEstoqueResponse> resultado = movimentacaoEstoqueService.listarPorDeposito(propriedadeId, depositoId, usuarioId);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarMovimentacaoPorIdQuandoPertenceAoDeposito() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        Deposito deposito = criarDeposito(propriedadeId, depositoId);
        Produto produto = criarProduto();

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque(
            produto, deposito, TipoMovimentacao.ENTRADA, 10.0, LocalDate.now(), null, null
        );

        UUID movimentacaoId = UUID.randomUUID();

        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(movimentacaoEstoqueRepository.findById(movimentacaoId)).thenReturn(Optional.of(movimentacao));

        MovimentacaoEstoqueResponse response = movimentacaoEstoqueService.buscarPorId(propriedadeId, depositoId, movimentacaoId, usuarioId);

        assertEquals(TipoMovimentacao.ENTRADA, response.tipo());
    }

    @Test
    void deveRecusarBuscarMovimentacaoDeOutroDeposito() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID depositoId = UUID.randomUUID();

        Deposito deposito = criarDeposito(propriedadeId, depositoId);
        Deposito outroDeposito = criarDeposito(propriedadeId, UUID.randomUUID());
        Produto produto = criarProduto();

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque(
            produto, outroDeposito, TipoMovimentacao.ENTRADA, 10.0, LocalDate.now(), null, null
        );

        UUID movimentacaoId = UUID.randomUUID();

        when(depositoRepository.findById(depositoId)).thenReturn(Optional.of(deposito));
        when(movimentacaoEstoqueRepository.findById(movimentacaoId)).thenReturn(Optional.of(movimentacao));

        assertThrows(RegraDeNegocioException.class,
            () -> movimentacaoEstoqueService.buscarPorId(propriedadeId, depositoId, movimentacaoId, usuarioId));
    }
}
