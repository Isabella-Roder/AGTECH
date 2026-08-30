package com.AGTECH.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Talhao;

public interface TalhaoRepository extends JpaRepository<Talhao, Long> {
    
    List<Talhao> findByPropriedadeId(Long propriedadeId);
}
