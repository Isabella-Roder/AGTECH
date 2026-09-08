package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.AtividadeDeCampoResponse;
import com.AGTECH.backend.dtos.CadastroAtividadeDeCampoRequest;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.AtividadeDeCampo;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.repository.AtividadeDeCampoRepository;
import com.AGTECH.backend.repository.SafraRepository;

@Service 
public class AtividadeDeCampoService {
    
    private final AtividadeDeCampoRepository campoRepository;
    private final SafraRepository safraRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public AtividadeDeCampoService(
        AtividadeDeCampoRepository campoRepository,
        SafraRepository safraRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.campoRepository = campoRepository;
        this.safraRepository = safraRepository;
        this.acessoService = acessoService;
    }

    private AtividadeDeCampo buscarEntidade(UUID id) {
        return campoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Atividade de campo não encontrada com ID: " + id));
    }

    private Safra buscarSafraId(UUID safraId) {
        return safraRepository.findById(safraId)
            .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + safraId));
    }

    private void verificarSafraPertence(UUID talhaoId, UUID propriedadeId, Safra safra) {
        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        } else if (!safra.getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }
    }

    
    private void verificarAtividadePertencente(UUID propriedadeId, UUID talhaoId, UUID safraId, AtividadeDeCampo atividade) {
        if (!atividade.getSafra().getId().equals(safraId)) {
            throw new RegraDeNegocioException("Essa atividade não pertence a essa safra.");
        } else if (!atividade.getSafra().getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        } else if (!atividade.getSafra().getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }
    }

    @Transactional 
    public AtividadeDeCampoResponse cadastrar(UUID propriedadeId, UUID talhaoId, UUID safraId, CadastroAtividadeDeCampoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Safra safra = buscarSafraId(safraId);

        verificarSafraPertence(talhaoId, propriedadeId, safra);

        AtividadeDeCampo atividade = new AtividadeDeCampo(
            safra,
            request.dataRealizacao(),
            request.tipo(),
            request.observacoes()
        );

        return AtividadeDeCampoResponse.from(campoRepository.save(atividade));
    }

    @Transactional(readOnly = true)
    public List<AtividadeDeCampoResponse> listarPorSafra(UUID propriedadeId, UUID talhaoId, UUID safraId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Safra safra = buscarSafraId(safraId);
        verificarSafraPertence(talhaoId, propriedadeId, safra);
        return campoRepository.findBySafraId(safraId)
            .stream().map(AtividadeDeCampoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AtividadeDeCampoResponse buscarPorId(UUID propriedadeId, UUID talhaoId, UUID safraId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        AtividadeDeCampo atividade = buscarEntidade(id);
        verificarAtividadePertencente(propriedadeId, talhaoId, safraId, atividade);
        return AtividadeDeCampoResponse.from(atividade);
    }
}
