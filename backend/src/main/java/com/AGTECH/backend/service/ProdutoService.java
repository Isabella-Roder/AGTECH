package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroProdutoRequest;
import com.AGTECH.backend.dtos.ProdutoResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Produto;
import com.AGTECH.backend.repository.ProdutoRepository;

@Service 
public class ProdutoService {
    
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    private Produto buscarEntidade(UUID id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado com ID: " + id));
    }

    @Transactional 
    public ProdutoResponse cadastrar(CadastroProdutoRequest request) {
        if (produtoRepository.existsByNome(request.nome())) {
            throw new RegraDeNegocioException("Produto já cadastrado.");
        }

        Produto produto = new Produto(
            request.nome(),
            request.unidadeMedida(),
            request.categoria()
        );

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional 
    public ProdutoResponse desativar(UUID id) {
        Produto produto = buscarEntidade(id);
        produto.desativar();
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional 
    public ProdutoResponse ativar(UUID id) {
        Produto produto = buscarEntidade(id);
        produto.ativar();
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAll()
            .stream().map(ProdutoResponse::from).toList();
    }
}
