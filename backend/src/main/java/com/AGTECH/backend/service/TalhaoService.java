package com.AGTECH.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroTalhaoRequest;
import com.AGTECH.backend.dtos.TalhaoResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.TalhaoRepository;

@Service
public class TalhaoService {
    
    private final TalhaoRepository talhaoRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public TalhaoService(
        TalhaoRepository talhaoRepository,
        PropriedadeRuralRepository propriedadeRuralRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.talhaoRepository = talhaoRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoService = acessoService;
    }

    private Talhao buscarEntidade(Long id) {
        return talhaoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Talhão não encontrado com ID: " + id));
    }

    @Transactional
    public TalhaoResponse cadastrar(Long propriedadeId, CadastroTalhaoRequest request, Long usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        PropriedadeRural propriedade = propriedadeRuralRepository.findById(propriedadeId)
            .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrado com ID: " + propriedadeId));

        Talhao talhao = new Talhao(propriedade, request.nome(), request.areaHectares());

        return TalhaoResponse.from(talhaoRepository.save(talhao));
    }

    @Transactional
    public TalhaoResponse atualizar(Long propriedadeId, Long talhaoId, CadastroTalhaoRequest request, Long usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = buscarEntidade(talhaoId);
        if (!talhao.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse talhão não pertence a essa propriedade.");
        }

        talhao.setNome(request.nome());
        talhao.setAreaHectares(request.areaHectares());

        return TalhaoResponse.from(talhao);
    }

    @Transactional
    public TalhaoResponse desativar(Long propriedadeId, Long talhaoId, Long usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = buscarEntidade(talhaoId);
        talhao.desativar();

        return TalhaoResponse.from(talhaoRepository.save(talhao));
    }

    @Transactional
    public TalhaoResponse ativar(Long propriedadeId, Long talhaoId, Long usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Talhao talhao = buscarEntidade(talhaoId);
        talhao.ativar();

        return TalhaoResponse.from(talhaoRepository.save(talhao));
    }


    @Transactional(readOnly = true)
    public List<TalhaoResponse> listarPorPropriedade(Long propriedadeId, Long usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        return talhaoRepository.findByPropriedadeId(propriedadeId).stream()
            .map(TalhaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TalhaoResponse buscarPorId(Long id) {
        return  TalhaoResponse.from(buscarEntidade(id));
    }
}
