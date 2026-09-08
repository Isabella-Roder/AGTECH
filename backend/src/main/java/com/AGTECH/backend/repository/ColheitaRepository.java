package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Colheita;

public interface ColheitaRepository extends JpaRepository<Colheita, UUID> {
    
    List<Colheita> findBySafraId(UUID safraId);
}
