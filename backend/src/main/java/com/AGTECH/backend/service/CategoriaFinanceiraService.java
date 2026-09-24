package com.AGTECH.backend.service;

import com.AGTECH.backend.dtos.CadastroCategoriaFinanceiraRequest;
import com.AGTECH.backend.dtos.CategoriaFinanceiraResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.CategoriaFinanceira;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.CategoriaFinanceiraRepository;
import com.AGTECH.backend.repository.LancamentoFinanceiroRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaFinanceiraService {

    private final CategoriaFinanceiraRepository categoriaFinanceiraRepository;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public CategoriaFinanceiraService(
            CategoriaFinanceiraRepository categoriaFinanceiraRepository,
            LancamentoFinanceiroRepository lancamentoFinanceiroRepository,
            PropriedadeRuralRepository propriedadeRuralRepository,
            UsuarioPropriedadeAcessoService acessoService
    ) {
        this.categoriaFinanceiraRepository = categoriaFinanceiraRepository;
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoService = acessoService;
    }

    private CategoriaFinanceira buscarEntidade(UUID id) {
        return categoriaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Categoria financeira não encontrada com ID: " + id));
    }

    private PropriedadeRural buscarPropriedade(UUID propriedadeId) {
        return propriedadeRuralRepository.findById(propriedadeId)
                .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrada com ID: " + propriedadeId));
    }

    private void validarCategoriaExistente(CategoriaFinanceira categoriaFinanceira, UUID propriedadeId) {
        if (!categoriaFinanceira.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Essa categoria financeira não pertence a essa propriedade.");
        }
    }

    @Transactional
    public CategoriaFinanceiraResponse cadastrar(UUID propriedadeId, CadastroCategoriaFinanceiraRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        PropriedadeRural propriedade = buscarPropriedade(propriedadeId);

        CategoriaFinanceira categoria = new CategoriaFinanceira(
                propriedade,
                request.nome(),
                request.tipo()
        );

        return CategoriaFinanceiraResponse.from(categoriaFinanceiraRepository.save(categoria));
    }

    @Transactional
    public CategoriaFinanceiraResponse atualizar(UUID propriedadeId, UUID id, CadastroCategoriaFinanceiraRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        CategoriaFinanceira categoria = buscarEntidade(id);
        validarCategoriaExistente(categoria, propriedadeId);

        if (categoria.getTipo() != request.tipo() && lancamentoFinanceiroRepository.existsByCategoriaId(id)) {
            throw new RegraDeNegocioException("Não é possível alterar o tipo de uma categoria que já possui lançamentos.");
        }

        categoria.setNome(request.nome());
        categoria.setTipo(request.tipo());

        return CategoriaFinanceiraResponse.from(categoriaFinanceiraRepository.save(categoria));
    }

    @Transactional
    public CategoriaFinanceiraResponse desativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        CategoriaFinanceira categoria = buscarEntidade(id);
        validarCategoriaExistente(categoria, propriedadeId);
        categoria.desativar();
        return CategoriaFinanceiraResponse.from(categoriaFinanceiraRepository.save(categoria));
    }

    @Transactional
    public CategoriaFinanceiraResponse ativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        CategoriaFinanceira categoria = buscarEntidade(id);
        validarCategoriaExistente(categoria, propriedadeId);
        categoria.ativar();
        return CategoriaFinanceiraResponse.from(categoriaFinanceiraRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaFinanceiraResponse> listarPorPropriedade(UUID propriedadeId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        return categoriaFinanceiraRepository.findByPropriedadeId(propriedadeId)
                .stream().map(CategoriaFinanceiraResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaFinanceiraResponse buscarPorId(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        CategoriaFinanceira categoria = buscarEntidade(id);
        validarCategoriaExistente(categoria, propriedadeId);
        return CategoriaFinanceiraResponse.from(categoria);
    }
}
