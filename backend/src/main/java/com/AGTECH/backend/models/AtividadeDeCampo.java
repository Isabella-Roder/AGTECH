package com.AGTECH.backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoAtividade;

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
    name = "atividades_de_campo",
    indexes = {
        @Index(
            name = "idx_safra_atividade_campo",
            columnList = "fk_atividade_campo_safra"
        )
    }
)
public class AtividadeDeCampo {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_atividade_campo_safra",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_atividades_safra")
    )
    private Safra safra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAtividade tipo;

    @Column(nullable = false)
    private LocalDate dataRealizacao;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public AtividadeDeCampo() {

    }

    public AtividadeDeCampo(
        Safra safra,
        LocalDate dataRealizacao,
        TipoAtividade tipo,
        String observacoes
    ) {
        this.safra = safra;
        this.dataRealizacao = dataRealizacao;
        this.tipo = tipo;
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

    public TipoAtividade getTipo() {
        return tipo;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
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

    public void setTipo(TipoAtividade tipo) {
        this.tipo = tipo;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
