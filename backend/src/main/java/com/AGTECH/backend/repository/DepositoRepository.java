package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Deposito;

public interface DepositoRepository extends JpaRepository<Deposito, UUID> {
    
    List<Deposito> findByPropriedadeId(UUID propriedadeId);
}
