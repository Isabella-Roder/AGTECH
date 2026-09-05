package com.AGTECH.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Cultura;

public interface CulturaRepository extends JpaRepository<Cultura, UUID> {

    boolean existsByNome(String nome);
}
