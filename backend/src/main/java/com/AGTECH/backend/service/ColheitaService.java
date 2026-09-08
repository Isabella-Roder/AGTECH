package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroColheitaRequest;
import com.AGTECH.backend.dtos.ColheitaResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Colheita;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.repository.ColheitaRepository;
import com.AGTECH.backend.repository.SafraRepository;

@Service 
public class ColheitaService {
    
    private final ColheitaRepository colheitaRepository;
    private final SafraRepository safraRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public ColheitaService(
        ColheitaRepository colheitaRepository,
        SafraRepository safraRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.colheitaRepository = colheitaRepository;
        this.safraRepository = safraRepository;
        this.acessoService = acessoService;
    }

    private Colheita buscarEntidade(UUID id) {
        return colheitaRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Colheita não encontrada com ID: " + id));
    }

    private Safra buscarSafraId(UUID safraId) {
        return safraRepository.findById(safraId)
            .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + safraId));
    }

    private void verificarSafraPertencente(UUID propriedadeId, UUID talhaoId, Safra safra) {
        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        } else if (!safra.getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }
    }

    private void verificarColheitaPertencente(UUID propriedadeId, UUID talhaoId, UUID safraId, Colheita colheita) {
        if (!colheita.getSafra().getId().equals(safraId)) {
            throw new RegraDeNegocioException("Essa colheita não pertence a essa safra.");
        } else if (!colheita.getSafra().getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        } else if (!colheita.getSafra().getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }
    }

    @Transactional
    public ColheitaResponse cadastrar(UUID propriedadeId, UUID talhaoId, UUID safraId, CadastroColheitaRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Safra safra = buscarSafraId(safraId);

        verificarSafraPertencente(propriedadeId, talhaoId, safra);

        Colheita colheita = new Colheita(
            safra,
            request.dataColheita(),
            request.quantidadeColhida(),
            request.unidadeMedida(),
            request.observacoes()
        );

        return ColheitaResponse.from(colheitaRepository.save(colheita));
    }

    @Transactional(readOnly = true)
    public List<ColheitaResponse> listarPorSafra(UUID propriedadeId, UUID talhaoId, UUID safraId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Safra safra = buscarSafraId(safraId);

        verificarSafraPertencente(propriedadeId, talhaoId, safra);

        return colheitaRepository.findBySafraId(safraId)
            .stream().map(ColheitaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ColheitaResponse buscarPorId(UUID propriedadeId, UUID talhaoId, UUID safraId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Colheita colheita = buscarEntidade(id);

        verificarColheitaPertencente(propriedadeId, talhaoId, safraId, colheita);

        return ColheitaResponse.from(colheita);
    }
}
