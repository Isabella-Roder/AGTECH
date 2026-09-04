package com.AGTECH.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroCulturaRequest;
import com.AGTECH.backend.dtos.CulturaResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Cultura;
import com.AGTECH.backend.repository.CulturaRepository;

@Service 
public class CulturaService {
    
    private final CulturaRepository culturaRepository;

    public CulturaService(CulturaRepository culturaRepository) {
        this.culturaRepository = culturaRepository;
    }

    @Transactional 
    public CulturaResponse cadastrar(CadastroCulturaRequest request) {
        if (culturaRepository.existsByNome(request.nome())) {
            throw new RegraDeNegocioException("Cultura já cadastrada.");
        }

        Cultura cultura = new Cultura(request.nome());

        return CulturaResponse.from(culturaRepository.save(cultura));
    }

    @Transactional(readOnly = true)
    public List<CulturaResponse> listar() {
        return culturaRepository.findAll().stream()
            .map(CulturaResponse::from).toList();
    }
}
