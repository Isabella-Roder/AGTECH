package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.CadastroDepositoRequest;
import com.AGTECH.backend.dtos.DepositoResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Deposito;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.repository.DepositoRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;

@Service 
public class DepositoService {
    
    private final DepositoRepository depositoRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public DepositoService(
        DepositoRepository depositoRepository,
        PropriedadeRuralRepository propriedadeRuralRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.depositoRepository = depositoRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoService = acessoService;
    }

    private Deposito buscarEntidade(UUID id) {
        return depositoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Deposito não encontrado com ID: " + id));
    }

    private PropriedadeRural buscarPropriedadeId(UUID propriedadeId) {
        return propriedadeRuralRepository.findById(propriedadeId)
            .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrado com ID: " + propriedadeId));
    }

    private void verificaPropriedadePertenceADeposito(UUID propriedadeId, Deposito deposito) {

        if (!deposito.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse deposito não pertence a essa propriedade.");
        }
    }

    @Transactional 
    public DepositoResponse cadastrar(UUID propriedadeId, CadastroDepositoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        PropriedadeRural propriedade = buscarPropriedadeId(propriedadeId);

        Deposito deposito = new Deposito(
            propriedade,
            request.nome()
        );

        return DepositoResponse.from(depositoRepository.save(deposito));
    }

    @Transactional 
    public DepositoResponse atualizar(UUID propriedadeId, UUID id, CadastroDepositoRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
                
        Deposito deposito = buscarEntidade(id);

        verificaPropriedadePertenceADeposito(propriedadeId, deposito);

        deposito.setNome(request.nome());

        return DepositoResponse.from(deposito);
    }

    @Transactional 
    public DepositoResponse desativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Deposito deposito = buscarEntidade(id);
        verificaPropriedadePertenceADeposito(propriedadeId, deposito);
        deposito.desativar();
        return DepositoResponse.from(depositoRepository.save(deposito));
    }

    @Transactional 
    public DepositoResponse ativar(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Deposito deposito = buscarEntidade(id);
        verificaPropriedadePertenceADeposito(propriedadeId, deposito);
        deposito.ativar();
        return DepositoResponse.from(depositoRepository.save(deposito));
    }

    @Transactional(readOnly = true)
    public List<DepositoResponse> listarPorPropriedade(UUID propriedadeId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        return depositoRepository.findByPropriedadeId(propriedadeId)
            .stream().map(DepositoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DepositoResponse buscarPorId(UUID propriedadeId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Deposito deposito = buscarEntidade(id);
        verificaPropriedadePertenceADeposito(propriedadeId, deposito);
        return DepositoResponse.from(deposito);
    }
}
