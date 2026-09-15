package com.AGTECH.backend.service;

import com.AGTECH.backend.dtos.CadastroMaquinaRequest;
import com.AGTECH.backend.dtos.MaquinaResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Maquina;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.MaquinaRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaquinaService {

    private final MaquinaRepository maquinaRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public MaquinaService(
            MaquinaRepository maquinaRepository,
            PropriedadeRuralRepository propriedadeRuralRepository,
            UsuarioPropriedadeAcessoService acessoService
    ) {
        this.maquinaRepository = maquinaRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoService = acessoService;
    }

    private Maquina buscarEntidade(UUID id) {
        return maquinaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Maquina não encontrada com ID: " + id));
    }

    private PropriedadeRural buscarPropriedade(UUID propriedadeId) {
        return propriedadeRuralRepository.findById(propriedadeId)
                .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrada com ID: " + propriedadeId));
    }

    private void verificarMaquinaPertencente(Maquina maquina, UUID propriedadeId) {
        if (!maquina.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Essa maquina não pertence a essa propriedade.");
        }
    }

    @Transactional
    public MaquinaResponse cadastrar(UUID propriedadeId, CadastroMaquinaRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        PropriedadeRural propriedade = buscarPropriedade(propriedadeId);

        Maquina maquina = new Maquina(
                propriedade,
                request.identificador(),
                request.tipo(),
                request.horimetroAtual()
        );

        return MaquinaResponse.from(maquinaRepository.save(maquina));
    }

    @Transactional
    public MaquinaResponse desativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Maquina maquina = buscarEntidade(id);
        verificarMaquinaPertencente(maquina, propriedadeId);
        maquina.desativar();
        return MaquinaResponse.from(maquinaRepository.save(maquina));
    }

    @Transactional
    public MaquinaResponse ativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Maquina maquina = buscarEntidade(id);
        verificarMaquinaPertencente(maquina, propriedadeId);
        maquina.ativar();
        return MaquinaResponse.from(maquinaRepository.save(maquina));
    }

    @Transactional(readOnly = true)
    public List<MaquinaResponse> listarPorPropriedade(UUID propriedadeId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        return maquinaRepository.findByPropriedadeId(propriedadeId)
                .stream().map(MaquinaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MaquinaResponse buscarPorId(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Maquina maquina = buscarEntidade(id);
        verificarMaquinaPertencente(maquina, propriedadeId);
        return MaquinaResponse.from(maquina);
    }
}
