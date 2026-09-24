package com.AGTECH.backend.repository;

import com.AGTECH.backend.models.LancamentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, UUID> {

    List<LancamentoFinanceiro> findByPropriedadeId(UUID propriedadeId);

    List<LancamentoFinanceiro> findByCategoriaId(UUID categoriaId);

    List<LancamentoFinanceiro> findBySafraId(UUID safraId);

    boolean existsByCategoriaId(UUID categoriaId);
}
