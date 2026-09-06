package com.AGTECH.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    
    boolean existsByNome(String nome);

}
