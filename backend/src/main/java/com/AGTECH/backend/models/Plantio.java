package com.AGTECH.backend.models;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "plantios",
    indexes = {
        @Index (
            name = "idx_safra_plantio",
            columnList = "safra_id"
        )
    }
)
public class Plantio {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "safra_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_plantios_safra")
    )
    private Safra safra;

    @Column(nullable = false)
    private LocalDate dataPlantio;

    @Column(nullable = false)
    private Double areaPlantadaHectares;

    @Column(length = 500)
    private String observacoes;

    public Plantio() {

    }

    public Plantio(
        Safra safra,
        LocalDate dataPlantio,
        Double areaPlantadaHectares,
        String observacoes
    ) {
        this.safra = safra;
        this.dataPlantio = dataPlantio;
        this.areaPlantadaHectares = areaPlantadaHectares;
        this.observacoes = observacoes;
    }

    public UUID getId() {
        return id;
    }

    public Safra getSafra() {
        return safra;
    }

    public LocalDate getDataPlantio() {
        return dataPlantio;
    }

    public Double getAreaPlantadaHectares() {
        return areaPlantadaHectares;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setDataPlantio(LocalDate dataPlantio) {
        this.dataPlantio = dataPlantio;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
