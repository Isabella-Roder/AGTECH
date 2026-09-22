package com.AGTECH.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.Animal;

public interface AnimalRepository extends JpaRepository<Animal, UUID> {
    
    List<Animal> findByRebanhoId(UUID rebanhoId);
}
