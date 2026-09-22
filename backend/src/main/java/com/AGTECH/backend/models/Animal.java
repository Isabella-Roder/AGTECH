package com.AGTECH.backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.SexoAnimal;
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
    name = "animais",
    indexes = {
        @Index (
            name = "idx_rebanho_animal",
            columnList = "fk_animal_rebanho"
        )
    }
)
public class Animal {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_animal_rebanho",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_animal_rebanho")
    )
    private Rebanho rebanho;

    @Column(name = "identificacao", nullable = false, length = 50)
    private String identificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo_animal", nullable = false)
    private SexoAnimal sexo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Animal() {

    }

    public Animal(
        Rebanho rebanho,
        String identificacao,
        SexoAnimal sexo,
        LocalDate dataNascimento
    ) {
        this.rebanho = rebanho;
        this.identificacao = identificacao;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
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
            throw new RegraDeNegocioException("Animal precisa estar ativo para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Animal precisa estar desativado para ativar.");
        }

        ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public Rebanho getRebanho() {
        return rebanho;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public SexoAnimal getSexo() {
        return sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
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

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public void setSexo(SexoAnimal sexo) {
        this.sexo = sexo;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
