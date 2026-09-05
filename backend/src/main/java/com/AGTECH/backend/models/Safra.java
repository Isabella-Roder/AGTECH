package com.AGTECH.backend.models;

import java.time.LocalDate;
import java.util.UUID;

import com.AGTECH.backend.enums.StatusSafra;
import com.AGTECH.backend.exception.RegraDeNegocioException;

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
import jakarta.persistence.Table;

@Entity 
@Table(
    name = "safras",
    indexes = {
        @Index (
            name = "idx_talhao_safra",
            columnList = "fk_safra_talhao"
        ),
        @Index (
            name = "idx_cultura_safra",
            columnList = "fk_safra_cultura"
        )
    }
)
public class Safra {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_safra_talhao",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_talhao")
    )
    private Talhao talhao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_safra_cultura",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_cultura")
    ) 
    private Cultura cultura;

    @Column(nullable = false, length = 70)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFimPrevista;

    @Column
    private LocalDate dataFimReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSafra status = StatusSafra.PLANEJADA;

    public Safra() {

    }

    public Safra(
        Talhao talhao,
        Cultura cultura,
        String nome,
        LocalDate dataFimPrevista
    ) {
        this.talhao = talhao;
        this.cultura = cultura;
        this.nome = nome;
        this.dataFimPrevista = dataFimPrevista;
    }

    @PrePersist 
    private void antesDeSalvar() {
        dataInicio = LocalDate.now();
    }

    public void iniciar() {
        if (status != StatusSafra.PLANEJADA) {
            throw new RegraDeNegocioException("Safra precisa estar planejada para iniciar.");
        }

        status = StatusSafra.EM_ANDAMENTO;
    }

    public void finalizar() {
        if (status != StatusSafra.EM_ANDAMENTO) {
            throw new RegraDeNegocioException("Safra precisa estar em andamento para finalizar.");
        }

        status = StatusSafra.FINALIZADA;
        dataFimReal = LocalDate.now();
    }

    public void cancelar() {
        if (status == StatusSafra.FINALIZADA || status == StatusSafra.CANCELADA) {
            throw new RegraDeNegocioException("Safra já encerrada, não pode ser cancelada.");
        }

        status = StatusSafra.CANCELADA;
    }

    public UUID getId() {
        return id;
    }

    public Talhao getTalhao() {
        return talhao;
    }

    public Cultura getCultura() {
        return cultura;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFimPrevista() {
        return dataFimPrevista;
    }

    public LocalDate getDataFimReal() {
        return dataFimReal;
    }

    public StatusSafra getStatus() {
        return status;
    }

    public void setNome(String nome) {
        this.nome = nome;
    } 

    public void setDataFimPrevista(LocalDate dataFimPrevista) {
        this.dataFimPrevista = dataFimPrevista;
    }

    public void setStatus(StatusSafra status) {
        this.status = status;
    }
}
