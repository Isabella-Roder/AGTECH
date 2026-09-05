package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroSafraRequest;
import com.AGTECH.backend.dtos.SafraResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Cultura;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.CulturaRepository;
import com.AGTECH.backend.repository.SafraRepository;
import com.AGTECH.backend.repository.TalhaoRepository;

@Service 
public class SafraService {
    
    private final SafraRepository safraRepository;
    private final UsuarioPropriedadeAcessoService acessoService;
    private final TalhaoRepository talhaoRepository;
    private final CulturaRepository culturaRepository;

    public SafraService(SafraRepository safraRepository, UsuarioPropriedadeAcessoService acessoService, TalhaoRepository talhaoRepository, CulturaRepository culturaRepository) {
        this.safraRepository = safraRepository;
        this.acessoService = acessoService;
        this.talhaoRepository = talhaoRepository;
        this.culturaRepository = culturaRepository;
    }

    private Safra buscarEntidade(UUID id) {
        return safraRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + id));
    }

    @Transactional 
    public SafraResponse cadastrar(UUID propriedadeId, UUID talhaoId, CadastroSafraRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = talhaoRepository.findById(talhaoId)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + talhaoId));

        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        Cultura cultura = culturaRepository.findById(request.culturaId())
            .orElseThrow(() -> new RegraDeNegocioException("Cultura não encontrada com ID: " + request.culturaId()));

        Safra safra = new Safra(
            talhao,
            cultura,
            request.nome(),
            request.dataFimPrevista()
        );

        return SafraResponse.from(safraRepository.save(safra));
    }

    @Transactional 
    public SafraResponse iniciar(UUID propriedadeId, UUID talhaoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = talhaoRepository.findById(talhaoId)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + talhaoId));

        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        Safra safra = buscarEntidade(id);
        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }
        safra.iniciar();

        return SafraResponse.from(safraRepository.save(safra));
    }

    @Transactional 
    public SafraResponse finalizar(UUID propriedadeId, UUID talhaoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = talhaoRepository.findById(talhaoId)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + talhaoId));

        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        Safra safra = buscarEntidade(id);
        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }
        safra.finalizar();

        return SafraResponse.from(safraRepository.save(safra));
    }

    @Transactional 
    public SafraResponse cancelar(UUID propriedadeId, UUID talhaoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = talhaoRepository.findById(talhaoId)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + talhaoId));

        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        Safra safra = buscarEntidade(id);
        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }
        safra.cancelar();

        return SafraResponse.from(safraRepository.save(safra));
    }

    @Transactional(readOnly = true)
    public List<SafraResponse> listar(UUID propriedadeId, UUID talhaoId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = talhaoRepository.findById(talhaoId)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + talhaoId));

        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        return safraRepository.findByTalhaoId(talhaoId)
            .stream().map(SafraResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SafraResponse buscarPorId(UUID propriedadeId, UUID talhaoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = talhaoRepository.findById(talhaoId)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + talhaoId));

        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        Safra safra = buscarEntidade(id);
        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }

        return SafraResponse.from(safra);
    }
}
