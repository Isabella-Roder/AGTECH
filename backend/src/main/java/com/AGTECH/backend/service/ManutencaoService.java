package com.AGTECH.backend.service;

import com.AGTECH.backend.dtos.CadastroManutencaoRequest;
import com.AGTECH.backend.dtos.ManutencaoResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Manutencao;
import com.AGTECH.backend.models.Maquina;
import com.AGTECH.backend.repository.ManutencaoRepository;
import com.AGTECH.backend.repository.MaquinaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final MaquinaRepository maquinaRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public ManutencaoService(
            ManutencaoRepository manutencaoRepository,
            MaquinaRepository maquinaRepository,
            UsuarioPropriedadeAcessoService acessoService
    ) {
        this.manutencaoRepository = manutencaoRepository;
        this.maquinaRepository = maquinaRepository;
        this.acessoService = acessoService;
    }

    private Manutencao buscarEntidade(UUID id) {
        return manutencaoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Manutenção não encontrada com ID: " + id));
    }

    private Maquina buscarMaquina(UUID maquinaId) {
        return maquinaRepository.findById(maquinaId)
            .orElseThrow(() -> new RegraDeNegocioException("Maquina não encontrada com ID: " + maquinaId));
    }

    private void verificarManutencaoPertencente(Manutencao manutencao, UUID propriedadeId, UUID maquinaId) {
        if (!manutencao.getMaquina().getId().equals(maquinaId)) {
            throw new RegraDeNegocioException("Essa manutenção não pertence a essa maquina.");
        } else if (!manutencao.getMaquina().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Essa maquina não pertence a essa propriedade.");
        }
    }

    private void verificarMaquinaPertencente(Maquina maquina, UUID propriedadeId) {
        if (!maquina.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Essa maquina não pertence a essa propriedade.");
        }
    }

    @Transactional 
    public ManutencaoResponse cadastrar(UUID propriedadeId, UUID maquinaId, CadastroManutencaoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Maquina maquina = buscarMaquina(maquinaId);
        
        verificarMaquinaPertencente(maquina, propriedadeId);

        Manutencao manutencao = new Manutencao(
            maquina,
            request.tipo(),
            request.dataRealizacao(),
            request.horimetroNoMomento(),
            request.descricao()
        );

        return ManutencaoResponse.from(manutencaoRepository.save(manutencao));
    }

    @Transactional
    public ManutencaoResponse atualizar(UUID propriedadeId, UUID maquinaId, UUID id, CadastroManutencaoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Manutencao manutencao = buscarEntidade(id);

        verificarManutencaoPertencente(manutencao, propriedadeId, maquinaId);

        manutencao.setTipo(request.tipo());
        manutencao.setDataRealizacao(request.dataRealizacao());
        manutencao.setHorimetroNoMomento(request.horimetroNoMomento());
        manutencao.setDescricao(request.descricao());

        return ManutencaoResponse.from(manutencaoRepository.save(manutencao));
    }

    @Transactional(readOnly = true)
    public List<ManutencaoResponse> listarPorMaquina(UUID propriedadeId, UUID maquinaId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Maquina maquina = buscarMaquina(maquinaId);
        verificarMaquinaPertencente(maquina, propriedadeId);
        return manutencaoRepository.findByMaquinaId(maquinaId)
            .stream().map(ManutencaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ManutencaoResponse buscarPorId(UUID propriedadeId, UUID maquinaId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Manutencao manutencao = buscarEntidade(id);
        verificarManutencaoPertencente(manutencao, propriedadeId, maquinaId);
        return ManutencaoResponse.from(manutencao);
    }
}
