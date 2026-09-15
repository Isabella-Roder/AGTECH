package com.AGTECH.backend.repository;

import com.AGTECH.backend.models.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ManutencaoRepository extends JpaRepository<Manutencao, UUID> {

    List<Manutencao> findByMaquinaId(UUID maquinaId);
}
