package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroPlantioRequest;
import com.AGTECH.backend.dtos.PlantioResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Plantio;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.repository.PlantioRepository;
import com.AGTECH.backend.repository.SafraRepository;

@Service 
public class PlantioService {
    
    private final PlantioRepository plantioRepository;
    private final SafraRepository safraRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public PlantioService(
        PlantioRepository plantioRepository,
        SafraRepository safraRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.plantioRepository = plantioRepository;
        this.safraRepository = safraRepository;
        this.acessoService = acessoService;
    }

    private Plantio buscarEntidade(UUID id) {
        return plantioRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Plantio não encontrado com ID: " + id));
    }

    @Transactional 
    public PlantioResponse cadastrar(UUID propriedadeId, UUID talhaoId, UUID safraId, CadastroPlantioRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Safra safra = safraRepository.findById(safraId)
            .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + safraId));

        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }

        if (!safra.getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        Plantio plantio = new Plantio(
            safra,
            request.dataPlantio(),
            request.areaPlantadaHectares(),
            request.observacoes()
        );

        return PlantioResponse.from(plantioRepository.save(plantio));
    }

    @Transactional 
    public PlantioResponse atualizar(UUID propriedadeId, UUID talhaoId, UUID safraId, UUID id, CadastroPlantioRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Plantio plantio = buscarEntidade(id);

        if (!plantio.getSafra().getId().equals(safraId)) {
            throw new RegraDeNegocioException("Esse plantio não pertence a essa safra.");
        }

        if (!plantio.getSafra().getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }

        if (!plantio.getSafra().getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        plantio.setDataPlantio(request.dataPlantio());
        plantio.setObservacoes(request.observacoes());

        return PlantioResponse.from(plantio);
    }

    @Transactional(readOnly = true)
    public List<PlantioResponse> listar(UUID propriedadeId, UUID safraId, UUID talhaoId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Safra safra = safraRepository.findById(safraId)
            .orElseThrow(() -> new RegraDeNegocioException("Safra não encontrada com ID: " + safraId));

        if (!safra.getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Essa safra não pertence a esse talhão.");
        }

        if (!safra.getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        return plantioRepository.findBySafraId(safraId)
            .stream().map(PlantioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PlantioResponse buscarPorId(UUID propriedadeId, UUID safraId, UUID talhaoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Plantio plantio = buscarEntidade(id);

        if (!plantio.getSafra().getId().equals(safraId)) {
            throw new RegraDeNegocioException("Esse plantio não pertence a essa safra.");
        }

        if (!plantio.getSafra().getTalhao().getId().equals(talhaoId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa safra.");
        }

        if (!plantio.getSafra().getTalhao().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        return PlantioResponse.from(plantio);
    }
}
