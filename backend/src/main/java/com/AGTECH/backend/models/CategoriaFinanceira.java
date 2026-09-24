package com.AGTECH.backend.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoLancamento;
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
    name = "categorias_financeiras",
    indexes = {
        @Index (
            name = "idx_propriedade_categoria_financeira",
            columnList = "fk_categoria_financeira_propriedade"
        )
    }
)
public class CategoriaFinanceira {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_categoria_financeira_propriedade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_categoria_financeira_propriedade")
    )
    private PropriedadeRural propriedade;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoLancamento tipo;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public CategoriaFinanceira() {

    }

    public CategoriaFinanceira(
        PropriedadeRural propriedade,
        String nome,
        TipoLancamento tipo
    ) {
        this.propriedade = propriedade;
        this.nome = nome;
        this.tipo = tipo;
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
            throw new RegraDeNegocioException("Categoria financeira precisa estar ativa para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Categoria financeira precisa estar desativada para ativar.");
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

    public TipoLancamento getTipo() {
        return tipo;
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

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }
}
