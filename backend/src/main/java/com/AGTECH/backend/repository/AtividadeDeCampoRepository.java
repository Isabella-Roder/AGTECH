package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.AtividadeDeCampo;

public interface AtividadeDeCampoRepository extends JpaRepository<AtividadeDeCampo, UUID> {
    
    List<AtividadeDeCampo> findBySafraId(UUID safraId);
}
