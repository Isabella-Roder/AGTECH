package com.AGTECH.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroPropriedadeRequest;
import com.AGTECH.backend.dtos.PropriedadeResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;

@Service
public class PropriedadeRuralService {
    
    private final PropriedadeRuralRepository propriedadeRuralRepository;

    public PropriedadeRuralService(
        PropriedadeRuralRepository propriedadeRuralRepository
    ) {
        this.propriedadeRuralRepository = propriedadeRuralRepository;
    }

    private PropriedadeRural buscarEntidade(Long id) {
        return propriedadeRuralRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Propriedade rural não encontrada com ID: " + id));
    }

    @Transactional
    public PropriedadeResponse cadastrar(CadastroPropriedadeRequest request) {
        PropriedadeRural propriedade = new PropriedadeRural();
        propriedade.setNome(request.nome());
        propriedade.setMunicipio(request.municipio());
        propriedade.setEstado(request.estado());
        propriedade.setAreaTotalHectares(request.areaTotalHectares());

        PropriedadeRural salva = propriedadeRuralRepository.save(propriedade);

        return PropriedadeResponse.from(salva);
    }

    @Transactional
    public PropriedadeResponse atualizar(Long id, CadastroPropriedadeRequest request) {
        PropriedadeRural propriedade = buscarEntidade(id);

        propriedade.setNome(request.nome());
        propriedade.setMunicipio(request.municipio());
        propriedade.setEstado(request.estado());
        propriedade.setAreaTotalHectares(request.areaTotalHectares());

        return PropriedadeResponse.from(propriedade);
    }

    @Transactional
    public PropriedadeResponse desativar(Long id) {
        PropriedadeRural propriedade = buscarEntidade(id);
        propriedade.desativar();
        return PropriedadeResponse.from(propriedadeRuralRepository.save(propriedade));
    }

    @Transactional
    public PropriedadeResponse ativar(Long id) {
        PropriedadeRural propriedade = buscarEntidade(id);
        propriedade.ativar();
        return PropriedadeResponse.from(propriedadeRuralRepository.save(propriedade));
    }

    @Transactional(readOnly = true)
    public PropriedadeResponse buscarPorId(Long id) {
        return PropriedadeResponse.from(buscarEntidade(id));
    }
}
