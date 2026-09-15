package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.AbastecimentoResponse;
import com.AGTECH.backend.dtos.CadastroAbastecimentoRequest;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Abastecimento;
import com.AGTECH.backend.models.Maquina;
import com.AGTECH.backend.repository.AbastecimentoRepository;
import com.AGTECH.backend.repository.MaquinaRepository;

@Service 
public class AbastecimentoService {
    
    private final AbastecimentoRepository abastecimentoRepository;
    private final MaquinaRepository maquinaRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public AbastecimentoService(
        AbastecimentoRepository abastecimentoRepository,
        MaquinaRepository maquinaRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.maquinaRepository = maquinaRepository;
        this.acessoService = acessoService;
    }

    private Abastecimento buscarEntidade(UUID id) {
        return abastecimentoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Abastecimento não encontrado com ID: " + id));
    }

    private Maquina buscarMaquina(UUID maquinaId) {
        return maquinaRepository.findById(maquinaId)
            .orElseThrow(() -> new RegraDeNegocioException("Maquina não encontrado com ID: " + maquinaId));
    }

    private void verificarAbastecimentoPertencente(Abastecimento abastecimento, UUID propriedadeId, UUID maquinaId) {
        if (!abastecimento.getMaquina().getId().equals(maquinaId)) {
            throw new RegraDeNegocioException("Abastecimento não pertence a essa maquina.");
        } else if (!abastecimento.getMaquina().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Maquina não pertence a essa propriedade.");
        }
    }

    private void verificarMaquinaPertencente(Maquina maquina, UUID propriedadeId) {
        if (!maquina.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Maquina não pertence a essa propriedade.");
        }
    }

    @Transactional
    public AbastecimentoResponse cadastrar(UUID propriedadeId, UUID maquinaId, CadastroAbastecimentoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Maquina maquina = buscarMaquina(maquinaId);
        verificarMaquinaPertencente(maquina, propriedadeId);

        Abastecimento abastecimento = new Abastecimento(
            maquina, 
            request.data(),
            request.litros(),
            request.horimetroNoMomento(),
            request.observacoes()
        );

        return AbastecimentoResponse.from(abastecimentoRepository.save(abastecimento));
    }

    @Transactional
    public AbastecimentoResponse atualizar(UUID propriedadeId, UUID maquinaId, UUID id, CadastroAbastecimentoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Abastecimento abastecimento = buscarEntidade(id);

        verificarAbastecimentoPertencente(abastecimento, propriedadeId, maquinaId);

        abastecimento.setData(request.data());
        abastecimento.setHorimetroNoMomento(request.horimetroNoMomento());
        abastecimento.setLitros(request.litros());
        abastecimento.setObservacoes(request.observacoes());

        return AbastecimentoResponse.from(abastecimentoRepository.save(abastecimento));
    }

    @Transactional(readOnly = true)
    public List<AbastecimentoResponse> listarPorMaquina(UUID propriedadeId, UUID maquinaId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Maquina maquina = buscarMaquina(maquinaId);
        verificarMaquinaPertencente(maquina, propriedadeId);
        return abastecimentoRepository.findByMaquinaId(maquinaId)
            .stream().map(AbastecimentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AbastecimentoResponse buscarPorId(UUID propriedadeId, UUID maquinaId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Abastecimento abastecimento = buscarEntidade(id);
        verificarAbastecimentoPertencente(abastecimento, propriedadeId, maquinaId);
        return AbastecimentoResponse.from(abastecimento);
    }
}
