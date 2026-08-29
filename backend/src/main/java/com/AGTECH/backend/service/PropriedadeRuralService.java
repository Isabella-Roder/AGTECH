package com.AGTECH.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroPropriedadeRequest;
import com.AGTECH.backend.dtos.PropriedadeResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.UsuarioPropriedadeAcessoRepository;

@Service
public class PropriedadeRuralService {
    
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoRepository acessoRepository;

    public PropriedadeRuralService(
        PropriedadeRuralRepository propriedadeRuralRepository,
        UsuarioPropriedadeAcessoRepository acessoRepository
    ) {
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoRepository = acessoRepository;
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

    @Transactional(readOnly = true)
    public List<PropriedadeResponse> listarMinhas(Long usuarioId) {
        return acessoRepository.findByUsuarioId(usuarioId)
            .stream().map(acesso -> PropriedadeResponse.from(acesso.getPropriedade())).toList();
    }
}
