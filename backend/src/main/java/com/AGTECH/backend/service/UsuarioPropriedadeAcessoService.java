package com.AGTECH.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.AcessoResponse;
import com.AGTECH.backend.dtos.ConcederAcessoRequest;
import com.AGTECH.backend.exception.AcessoNegadoException;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Usuario;
import com.AGTECH.backend.models.UsuarioPropriedadeAcesso;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.UsuarioPropriedadeAcessoRepository;
import com.AGTECH.backend.repository.UsuarioRepository;

@Service
public class UsuarioPropriedadeAcessoService {
    
    private final UsuarioPropriedadeAcessoRepository acessoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;

    public UsuarioPropriedadeAcessoService(
        UsuarioPropriedadeAcessoRepository acessoRepository,
        UsuarioRepository usuarioRepository,
        PropriedadeRuralRepository propriedadeRuralRepository
    ) {
        this.acessoRepository = acessoRepository;
        this.usuarioRepository = usuarioRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
    }

    @Transactional
    public AcessoResponse conceder(Long propriedadeId, ConcederAcessoRequest request) {
        if (acessoRepository.existsByUsuarioIdAndPropriedadeId(request.usuarioId(), propriedadeId)) {
            throw new RegraDeNegocioException("Usuário já tem acesso a essa propriedade.");
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
            .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado com ID: " + request.usuarioId()));

        PropriedadeRural propriedade = propriedadeRuralRepository.findById(propriedadeId)
            .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrado com ID: " + propriedadeId));

        UsuarioPropriedadeAcesso acesso = new UsuarioPropriedadeAcesso(usuario, propriedade, request.papel());

        return AcessoResponse.from(acessoRepository.save(acesso));
    }

    @Transactional
    public void revogar(Long propriedadeId, Long acessoId) {
        UsuarioPropriedadeAcesso acesso = acessoRepository.findById(acessoId)
            .orElseThrow(() -> new RegraDeNegocioException("Acesso não encontrado com ID: " + acessoId));

        if (!acesso.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse acesso não pertence a essa propriedade.");
        }

        acessoRepository.delete(acesso);
    }

    @Transactional(readOnly = true)
    public List<AcessoResponse> listarPorPropriedade(Long propriedadeId) {
        return acessoRepository.findByPropriedadeId(propriedadeId).stream()
            .map(AcessoResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public void verificarAcesso(Long usuarioId, Long propriedadeId) {
        if (!acessoRepository.existsByUsuarioIdAndPropriedadeId(usuarioId, propriedadeId)) {
            throw new AcessoNegadoException("Você não tem acesso a essa propriedade.");
        }
    }
}
