package com.AGTECH.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Cultura;

public interface CulturaRepository extends JpaRepository<Cultura, Long> {
    
    boolean existsByNome(String nome);
}
