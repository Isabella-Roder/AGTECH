package com.AGTECH.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.AtualizacaoUsuarioRequest;
import com.AGTECH.backend.dtos.CadastroUsuarioRequest;
import com.AGTECH.backend.dtos.UsuarioResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Usuario;
import com.AGTECH.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Usuario não encontrado com ID: " + id));
    }

    private void verificaEmailExistente(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraDeNegocioException("Email já cadastrado.");
        }
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        verificaEmailExistente(request.email());

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));

        Usuario salvo = usuarioRepository.save(usuario);

        return UsuarioResponse.from(salvo);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, AtualizacaoUsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);

        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new RegraDeNegocioException("Email já cadastrado.");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());

        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse desativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.desativar();
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponse.from(salvo);
    }

    @Transactional
    public UsuarioResponse ativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.ativar();
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(buscarEntidade(id));
    }
}
