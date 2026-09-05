package com.AGTECH.backend.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.exception.RegraDeNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "propriedades_rurais")
public class PropriedadeRural {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 80)
    private String municipio;

    @Column(nullable = false, length = 80)
    private String estado;

    @Column(nullable = false)
    private Double areaTotalHectares;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public PropriedadeRural() {

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
            throw new RegraDeNegocioException("Propriedade precisa estar ativa para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Propriedade precisa estar desativada para ativar.");   
        }

        ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getEstado() {
        return estado;
    }

    public Double getAreaTotalHectares() {
        return areaTotalHectares;
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

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setAreaTotalHectares(Double areaTotalHectares) {
        this.areaTotalHectares = areaTotalHectares;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
