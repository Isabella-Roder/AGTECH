package com.AGTECH.backend.repository;

import com.AGTECH.backend.models.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaquinaRepository extends JpaRepository<Maquina, UUID> {

    List<Maquina> findByPropriedadeId(UUID propriedadeId);
}
