package com.AGTECH.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AGTECH.backend.models.PropriedadeRural;

public interface PropriedadeRuralRepository extends JpaRepository<PropriedadeRural, UUID>{

}
