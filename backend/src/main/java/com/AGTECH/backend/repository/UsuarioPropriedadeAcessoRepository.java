package com.AGTECH.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.UsuarioPropriedadeAcesso;

public interface UsuarioPropriedadeAcessoRepository extends JpaRepository<UsuarioPropriedadeAcesso, UUID> {

    List<UsuarioPropriedadeAcesso> findByUsuarioId(UUID usuarioId);

    List<UsuarioPropriedadeAcesso> findByPropriedadeId(UUID propriedadeId);

    Optional<UsuarioPropriedadeAcesso> findByUsuarioIdAndPropriedadeId(UUID usuarioId, UUID propriedadeId);

    boolean existsByUsuarioIdAndPropriedadeId(UUID usuarioId, UUID propriedadeId);
}
