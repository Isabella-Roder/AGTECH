package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroProdutoRequest;
import com.AGTECH.backend.dtos.ProdutoResponse;
import com.AGTECH.backend.enums.CategoriaProduto;
import com.AGTECH.backend.enums.UnidadeMedida;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Produto;
import com.AGTECH.backend.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveCadastrarProdutoQuandoNomeNaoExiste() {
        CadastroProdutoRequest request = new CadastroProdutoRequest("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);

        when(produtoRepository.existsByNome("Ureia")).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ProdutoResponse response = produtoService.cadastrar(request);

        assertEquals("Ureia", response.nome());
        assertEquals(UnidadeMedida.KG, response.unidadeMedida());
        assertEquals(CategoriaProduto.FERTILIZANTE, response.categoria());
        assertEquals(true, response.ativo());
    }

    @Test
    void deveRecusarCadastroDeProdutoJaExistente() {
        CadastroProdutoRequest request = new CadastroProdutoRequest("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);

        when(produtoRepository.existsByNome("Ureia")).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> produtoService.cadastrar(request));
    }

    @Test
    void deveDesativarProduto() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        ProdutoResponse response = produtoService.desativar(id);

        assertEquals(false, response.ativo());
    }

    @Test
    void deveRecusarDesativarProdutoJaDesativado() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);
        produto.desativar();

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));

        assertThrows(RegraDeNegocioException.class, () -> produtoService.desativar(id));
    }

    @Test
    void deveAtivarProduto() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);
        produto.desativar();

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        ProdutoResponse response = produtoService.ativar(id);

        assertEquals(true, response.ativo());
    }

    @Test
    void deveRecusarAtivarProdutoJaAtivo() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));

        assertThrows(RegraDeNegocioException.class, () -> produtoService.ativar(id));
    }

    @Test
    void deveListarTodosOsProdutos() {
        Produto produto1 = new Produto("Ureia", UnidadeMedida.KG, CategoriaProduto.FERTILIZANTE);
        Produto produto2 = new Produto("Diesel", UnidadeMedida.LITRO, CategoriaProduto.COMBUSTIVEL);

        when(produtoRepository.findAll()).thenReturn(List.of(produto1, produto2));

        List<ProdutoResponse> resultado = produtoService.listar();

        assertEquals(2, resultado.size());
    }
}
