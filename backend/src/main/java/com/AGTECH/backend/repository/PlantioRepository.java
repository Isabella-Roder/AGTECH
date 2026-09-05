package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Plantio;

public interface PlantioRepository extends JpaRepository<Plantio, UUID> {
    
    List<Plantio> findBySafraId(UUID safraId);
}
