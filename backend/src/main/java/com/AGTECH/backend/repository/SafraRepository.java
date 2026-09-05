package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Safra;

public interface SafraRepository extends JpaRepository<Safra, UUID>{
    
    List<Safra> findByTalhaoId(UUID talhaoId);

    List<Safra> findByCulturaId(UUID culturaId);
}
