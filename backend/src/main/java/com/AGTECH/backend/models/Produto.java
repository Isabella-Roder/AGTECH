package com.AGTECH.backend.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.CategoriaProduto;
import com.AGTECH.backend.enums.UnidadeMedida;
import com.AGTECH.backend.exception.RegraDeNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity 
@Table(
    name = "produtos"
)
public class Produto {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidadeMedida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProduto categoria;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public Produto() {

    }

    public Produto(
        String nome,
        UnidadeMedida unidadeMedida,
        CategoriaProduto categoria
    ) {
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.categoria = categoria;
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
            throw new RegraDeNegocioException("Produto precisa estar ativo para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Produto precisa estar desativado para ativar.");
        }

        ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
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

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

}
