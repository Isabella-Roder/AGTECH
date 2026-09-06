package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroMovimentacaoEstoqueRequest;
import com.AGTECH.backend.dtos.MovimentacaoEstoqueResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Deposito;
import com.AGTECH.backend.models.MovimentacaoEstoque;
import com.AGTECH.backend.models.Produto;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.repository.DepositoRepository;
import com.AGTECH.backend.repository.MovimentacaoEstoqueRepository;
import com.AGTECH.backend.repository.ProdutoRepository;
import com.AGTECH.backend.repository.SafraRepository;

@Service 
public class MovimentacaoEstoqueService {
    
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final DepositoRepository depositoRepository;
    private final ProdutoRepository produtoRepository;
    private final SafraRepository safraRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public MovimentacaoEstoqueService(
        MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
        DepositoRepository depositoRepository,
        ProdutoRepository produtoRepository,
        SafraRepository safraRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.depositoRepository = depositoRepository;
        this.produtoRepository = produtoRepository;
        this.safraRepository = safraRepository;
        this.acessoService = acessoService;
    }

    private MovimentacaoEstoque buscarEntidade(UUID id) {
        return movimentacaoEstoqueRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Movimentação não encontrada com ID: " + id));
    }

    @Transactional
    public MovimentacaoEstoqueResponse cadastrar(UUID propriedadeId, UUID depositoId, CadastroMovimentacaoEstoqueRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Deposito deposito = depositoRepository.findById(depositoId)
            .orElseThrow(() -> new RegraDeNegocioException("Deposito não encontrado com ID: " + depositoId));
        
        if (!deposito.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse deposito não pertence a essa propriedade.");
        }

        Produto produto = produtoRepository.findById(request.produtoId())
            .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado com ID: " + request.produtoId()));

        Safra safra = null;

        if (request.safraId() != null) {
            safra = safraRepository.findById(request.safraId())
                .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + request.safraId()));

            if (!safra.getTalhao().getPropriedade().getId().equals(propriedadeId)) {
                throw new RegraDeNegocioException("Essa safra não pertence a essa propriedade.");
            }
        }

        MovimentacaoEstoque movimentacaoEstoque = new MovimentacaoEstoque(
            produto,
            deposito,
            request.tipo(),
            request.quantidade(),
            request.data(),
            safra,
            request.observacoes()
        );

        return MovimentacaoEstoqueResponse.from(movimentacaoEstoqueRepository.save(movimentacaoEstoque));
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponse> listarPorDeposito(UUID propriedadeId, UUID depositoId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Deposito deposito = depositoRepository.findById(depositoId)
            .orElseThrow(() -> new RegraDeNegocioException("Deposito não encontrado com ID: " + depositoId));
        if (!deposito.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse deposito não pertence a essa propriedade.");
        }
        return movimentacaoEstoqueRepository.findByDepositoId(depositoId).stream()
            .map(MovimentacaoEstoqueResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoEstoqueResponse buscarPorId(UUID propriedadeId, UUID depositoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Deposito deposito = depositoRepository.findById(depositoId)
            .orElseThrow(() -> new RegraDeNegocioException("Deposito não encontrado com ID: " + depositoId));
        if (!deposito.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse deposito não pertence a essa propriedade.");
        }

        MovimentacaoEstoque movimentacao = buscarEntidade(id);
        if (!movimentacao.getDeposito().getId().equals(depositoId)) {
            throw new RegraDeNegocioException("Essa movimentação não pertence a esse depósito.");
        }

        return MovimentacaoEstoqueResponse.from(movimentacao);
    }
}
