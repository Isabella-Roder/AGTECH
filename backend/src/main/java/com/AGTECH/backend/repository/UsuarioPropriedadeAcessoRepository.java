package com.AGTECH.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.UsuarioPropriedadeAcesso;

public interface UsuarioPropriedadeAcessoRepository extends JpaRepository<UsuarioPropriedadeAcesso, Long> {
    
    List<UsuarioPropriedadeAcesso> findByUsuarioId(Long usuarioId);

    List<UsuarioPropriedadeAcesso> findByPropriedadeId(Long propriedadeId);

    Optional<UsuarioPropriedadeAcesso> findByUsuarioIdAndPropriedadeId(Long usuarioId, Long propriedadeId);

    boolean existsByUsuarioIdAndPropriedadeId(Long usuarioId, Long propriedadeId);
}
