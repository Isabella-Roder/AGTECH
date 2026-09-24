package com.AGTECH.backend.repository;

import com.AGTECH.backend.models.CategoriaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaFinanceiraRepository extends JpaRepository<CategoriaFinanceira, UUID> {

    List<CategoriaFinanceira> findByPropriedadeId(UUID propriedadeId);
}
