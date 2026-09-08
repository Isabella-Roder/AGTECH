package com.AGTECH.backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.UnidadeMedida;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "colheitas",
    indexes = {
        @Index (
            name = "idx_safra_colheita",
            columnList = "fk_colheita_safra"
        )
    }
)
public class Colheita {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_colheita_safra",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_colheitas_safra")
    )
    private Safra safra;

    @Column(nullable = false)
    private LocalDate dataColheita;

    @Column(nullable = false)
    private Double quantidadeColhida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidadeMedida;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public Colheita() {

    }

    public Colheita(
        Safra safra,
        LocalDate dataColheita,
        Double quantidadeColhida,
        UnidadeMedida unidadeMedida,
        String observacoes
    ) {
        this.safra = safra;
        this.dataColheita = dataColheita;
        this.quantidadeColhida = quantidadeColhida;
        this.unidadeMedida = unidadeMedida;
        this.observacoes = observacoes;
    }

    @PrePersist
    private void antesDeSalvar() {
        LocalDateTime agora = LocalDateTime.now();

        criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    private void antesDeAtualizar() {
        atualizadoEm = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Safra getSafra() {
        return safra;
    }

    public LocalDate getDataColheita() {
        return dataColheita;
    }

    public Double getQuantidadeColhida() {
        return quantidadeColhida;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setSafra(Safra safra) {
        this.safra = safra;
    }

    public void setDataColheita(LocalDate dataColheita) {
        this.dataColheita = dataColheita;
    }

    public void setQuantidadeColhida(Double quantidadeColhida) {
        this.quantidadeColhida = quantidadeColhida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }


}
