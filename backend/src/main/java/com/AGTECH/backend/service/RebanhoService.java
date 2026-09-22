package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroRebanhoRequest;
import com.AGTECH.backend.dtos.RebanhoResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Rebanho;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.RebanhoRepository;

@Service 
public class RebanhoService {
    
    private final RebanhoRepository rebanhoRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public RebanhoService(
        RebanhoRepository rebanhoRepository,
        PropriedadeRuralRepository propriedadeRuralRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.rebanhoRepository = rebanhoRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoService = acessoService;
    }

    private Rebanho buscarEntidade(UUID id) {
        return rebanhoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Rebanho não encontrado com ID: " + id));
    }

    private PropriedadeRural buscarPropriedade(UUID propriedadeId) {
        return propriedadeRuralRepository.findById(propriedadeId)
            .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrada com ID: " + propriedadeId));
    }

    private void verificarRebanhoPertencente(Rebanho rebanho, UUID propriedadeId) {
        if (!rebanho.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse rebanho não pertence a essa propriedade.");
        }
    }

    @Transactional
    public RebanhoResponse cadastrar(UUID propriedadeId, CadastroRebanhoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        PropriedadeRural propriedade = buscarPropriedade(propriedadeId);

        Rebanho rebanho = new Rebanho(
            propriedade,
            request.nome(),
            request.especie()
        );

        return RebanhoResponse.from(rebanhoRepository.save(rebanho));
    }

    @Transactional 
    public RebanhoResponse atualizar(UUID propriedadeId, UUID id, CadastroRebanhoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Rebanho rebanho = buscarEntidade(id);
        verificarRebanhoPertencente(rebanho, propriedadeId);

        rebanho.setNome(request.nome());
        rebanho.setEspecie(request.especie());

        return RebanhoResponse.from(rebanhoRepository.save(rebanho));
    }

    @Transactional 
    public RebanhoResponse desativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Rebanho rebanho = buscarEntidade(id);
        verificarRebanhoPertencente(rebanho, propriedadeId);
        rebanho.desativar();
        return RebanhoResponse.from(rebanhoRepository.save(rebanho));
    }

    @Transactional 
    public RebanhoResponse ativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Rebanho rebanho = buscarEntidade(id);
        verificarRebanhoPertencente(rebanho, propriedadeId);
        rebanho.ativar();
        return RebanhoResponse.from(rebanhoRepository.save(rebanho));
    }

    @Transactional(readOnly = true)
    public List<RebanhoResponse> listarPorPropriedade(UUID propriedadeId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        return rebanhoRepository.findByPropriedadeId(propriedadeId)
            .stream().map(RebanhoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RebanhoResponse buscarPorId(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Rebanho rebanho = buscarEntidade(id);
        verificarRebanhoPertencente(rebanho, propriedadeId);
        return RebanhoResponse.from(rebanho);
    }
}
