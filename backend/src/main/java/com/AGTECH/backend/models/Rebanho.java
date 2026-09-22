package com.AGTECH.backend.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.EspecieAnimal;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity 
@Table(
    name = "rebanhos",
    indexes = {
        @Index(
            name = "idx_propriedade_rebanho",
            columnList = "fk_rebanho_propriedade"
        )
    }
)
public class Rebanho {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_rebanho_propriedade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_rebanho_propriedade")
    )
    private PropriedadeRural propriedade;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false)
    private EspecieAnimal especie;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Rebanho() {

    }

    public Rebanho(
        PropriedadeRural propriedade,
        String nome,
        EspecieAnimal especie
    ) {
        this.propriedade = propriedade;
        this.nome = nome;
        this.especie = especie;
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

    public void desativar() {
        if (!ativo) {
            throw new RegraDeNegocioException("Rebanho precisa estar ativo para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Rebanho precisa estar desativado para ativar.");
        }

        ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public PropriedadeRural getPropriedade() {
        return propriedade;
    }

    public String getNome() {
        return nome;
    }

    public EspecieAnimal getEspecie() {
        return especie;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEspecie(EspecieAnimal especie) {
        this.especie = especie;
    }
}
