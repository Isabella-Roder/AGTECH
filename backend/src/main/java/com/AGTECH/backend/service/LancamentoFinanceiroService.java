package com.AGTECH.backend.service;

import com.AGTECH.backend.dtos.CadastroLancamentoFinanceiroRequest;
import com.AGTECH.backend.dtos.LancamentoFinanceiroResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.CategoriaFinanceira;
import com.AGTECH.backend.models.LancamentoFinanceiro;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.repository.CategoriaFinanceiraRepository;
import com.AGTECH.backend.repository.LancamentoFinanceiroRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.SafraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LancamentoFinanceiroService {

    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final CategoriaFinanceiraRepository categoriaFinanceiraRepository;
    private final SafraRepository safraRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public LancamentoFinanceiroService(
            LancamentoFinanceiroRepository lancamentoFinanceiroRepository,
            PropriedadeRuralRepository propriedadeRuralRepository,
            CategoriaFinanceiraRepository categoriaFinanceiraRepository,
            SafraRepository safraRepository,
            UsuarioPropriedadeAcessoService acessoService
    ) {
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.categoriaFinanceiraRepository = categoriaFinanceiraRepository;
        this.safraRepository = safraRepository;
        this.acessoService = acessoService;
    }

    private LancamentoFinanceiro buscarEntidade(UUID id) {
        return lancamentoFinanceiroRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Lançamento financeiro não encontrado com ID: " + id));
    }

    private PropriedadeRural buscarPropriedade(UUID propriedadeId) {
        return propriedadeRuralRepository.findById(propriedadeId)
                .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrada com ID: " + propriedadeId));
    }

    private CategoriaFinanceira buscarCategoria(UUID categoriaId) {
        return categoriaFinanceiraRepository.findById(categoriaId)
                .orElseThrow(() -> new RegraDeNegocioException("Categoria financeira não encontrada com ID: " + categoriaId));
    }

    private Safra buscarSafra(UUID safraId) {
        return safraRepository.findById(safraId)
                .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + safraId));
    }

    private void verificarCategoriaUtilizavel(CategoriaFinanceira categoria, UUID propriedadeId) {
        if (!categoria.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Essa categoria não pertence a essa propriedade.");
        }

        if (!categoria.isAtivo()) {
            throw new RegraDeNegocioException("Não é possível lançar em uma categoria desativada.");
        }
    }

    private void verificarSafraPertencente(Safra safra, UUID propriedadeId) {
        if (!safra.getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a essa propriedade.");
        }
    }

    private void verificarLancamentoPertencente(LancamentoFinanceiro lancamento, UUID propriedadeId) {
        if (!lancamento.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse lançamento não pertence a essa propriedade.");
        }
    }

    private Safra resolverSafra(UUID safraId, UUID propriedadeId) {
        if (safraId == null) {
            return null;
        }

        Safra safra = buscarSafra(safraId);
        verificarSafraPertencente(safra, propriedadeId);
        return safra;
    }

    @Transactional
    public LancamentoFinanceiroResponse cadastrar(UUID propriedadeId, CadastroLancamentoFinanceiroRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        PropriedadeRural propriedade = buscarPropriedade(propriedadeId);
        CategoriaFinanceira categoria = buscarCategoria(request.categoriaId());
        verificarCategoriaUtilizavel(categoria, propriedadeId);
        Safra safra = resolverSafra(request.safraId(), propriedadeId);

        LancamentoFinanceiro lancamento = new LancamentoFinanceiro(
                propriedade,
                categoria,
                request.valor(),
                request.data(),
                request.descricao(),
                safra
        );

        return LancamentoFinanceiroResponse.from(lancamentoFinanceiroRepository.save(lancamento));
    }

    @Transactional
    public LancamentoFinanceiroResponse atualizar(UUID propriedadeId, UUID id, CadastroLancamentoFinanceiroRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        LancamentoFinanceiro lancamento = buscarEntidade(id);
        verificarLancamentoPertencente(lancamento, propriedadeId);

        if (!lancamento.getCategoria().getId().equals(request.categoriaId())) {
            CategoriaFinanceira categoria = buscarCategoria(request.categoriaId());
            verificarCategoriaUtilizavel(categoria, propriedadeId);
            lancamento.setCategoria(categoria);
        }

        lancamento.setValor(request.valor());
        lancamento.setData(request.data());
        lancamento.setDescricao(request.descricao());
        lancamento.setSafra(resolverSafra(request.safraId(), propriedadeId));

        return LancamentoFinanceiroResponse.from(lancamentoFinanceiroRepository.save(lancamento));
    }

    @Transactional
    public LancamentoFinanceiroResponse desativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        LancamentoFinanceiro lancamento = buscarEntidade(id);
        verificarLancamentoPertencente(lancamento, propriedadeId);
        lancamento.desativar();
        return LancamentoFinanceiroResponse.from(lancamentoFinanceiroRepository.save(lancamento));
    }

    @Transactional
    public LancamentoFinanceiroResponse ativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        LancamentoFinanceiro lancamento = buscarEntidade(id);
        verificarLancamentoPertencente(lancamento, propriedadeId);
        lancamento.ativar();
        return LancamentoFinanceiroResponse.from(lancamentoFinanceiroRepository.save(lancamento));
    }

    @Transactional(readOnly = true)
    public List<LancamentoFinanceiroResponse> listarPorPropriedade(UUID propriedadeId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        return lancamentoFinanceiroRepository.findByPropriedadeId(propriedadeId)
                .stream().map(LancamentoFinanceiroResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public LancamentoFinanceiroResponse buscarPorId(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        LancamentoFinanceiro lancamento = buscarEntidade(id);
        verificarLancamentoPertencente(lancamento, propriedadeId);
        return LancamentoFinanceiroResponse.from(lancamento);
    }
}
