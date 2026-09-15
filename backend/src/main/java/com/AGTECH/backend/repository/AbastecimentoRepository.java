package com.AGTECH.backend.repository;

import com.AGTECH.backend.models.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, UUID> {

    List<Abastecimento> findByMaquinaId(UUID maquinaId);
}
