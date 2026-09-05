package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Talhao;

public interface TalhaoRepository extends JpaRepository<Talhao, UUID> {

    List<Talhao> findByPropriedadeId(UUID propriedadeId);
}
