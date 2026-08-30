package com.AGTECH.backend.models;

import java.time.LocalDateTime;

import com.AGTECH.backend.exception.RegraDeNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "talhoes"
)
public class Talhao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private PropriedadeRural propriedade;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private Double areaHectares;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public Talhao() {

    }

    public Talhao(
        PropriedadeRural propriedade,
        String nome,
        Double areaHectares
    ) {
        this.propriedade = propriedade;
        this.nome = nome;
        this.areaHectares = areaHectares;
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
        if (ativo != true) {
            throw new RegraDeNegocioException("Talhão precisa estar ativo para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo != false) {
            throw new RegraDeNegocioException("Talhão precisa estar desativado para ativar.");
        }

        ativo = true;
    }

    public Long getId() {
        return id;
    }

    public PropriedadeRural getPropriedade() {
        return propriedade;
    }

    public String getNome() {
        return nome;
    }

    public Double getAreaHectares() {
        return areaHectares;
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

    public void setAreaHectares(Double areaHectares) {
        this.areaHectares = areaHectares;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
