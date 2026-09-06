package com.AGTECH.backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.AGTECH.backend.enums.TipoMovimentacao;

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
@Table (
    name = "movimentacoes_estoque",
    indexes = {
        @Index (
            name = "idx_produto_movimentacao_estoque",
            columnList = "fk_movimentacao_estoque_produto"
        ),
        @Index (
            name = "idx_deposito_movimentacao_estoque",
            columnList = "fk_movimentacao_estoque_deposito"
        ),
        @Index (
            name = "idx_safra_movimentacao_estoque",
            columnList = "fk_movimentacao_estoque_safra"
        )
    }
)
public class MovimentacaoEstoque {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_movimentacao_estoque_produto",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_produto")
    )
    private Produto produto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_movimentacao_estoque_deposito",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_deposito")
    )
    private Deposito deposito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Double quantidade;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_movimentacao_estoque_safra",
        foreignKey = @ForeignKey(name = "fk_safra")
    )
    private Safra safra;

    @Column
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public MovimentacaoEstoque() {

    }

    public MovimentacaoEstoque(
        Produto produto,
        Deposito deposito,
        TipoMovimentacao tipo,
        Double quantidade,
        LocalDate data,
        Safra safra,
        String observacoes
    ) {
        this.produto = produto;
        this.deposito = deposito;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.data = data;
        this.safra = safra;
        this.observacoes = observacoes;
    }

    @PrePersist
    private void antesDeSalvar() {
        criadoEm = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public LocalDate getData() {
        return data;
    }

    public Safra getSafra() {
        return safra;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
