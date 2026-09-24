package com.AGTECH.backend.models;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.AGTECH.backend.exception.RegraDeNegocioException;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity 
@Table(
    name = "lancamentos_financeiros",
    indexes = {
        @Index(
            name = "idx_propriedade_lancamento_financeiro",
            columnList = "fk_lancamento_financeiro_propriedade"
        ),
        @Index (
            name = "idx_categoria_lancamento_financeiro",
            columnList = "fk_lancamento_financeiro_categoria"
        ),
        @Index(
            name = "idx_safra_lancamento_financeiro",
            columnList = "fk_lancamento_financeiro_safra"
        )
    }
)
public class LancamentoFinanceiro {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_lancamento_financeiro_propriedade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_lancamento_financeiro_propriedade")
    )
    private PropriedadeRural propriedade;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_lancamento_financeiro_categoria",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_lancamento_financeiro_categoria")
    )
    private CategoriaFinanceira categoria;

    @Column(name = "valor", nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_lancamento_financeiro_safra",
        nullable = true,
        foreignKey = @ForeignKey(name = "fk_lancamento_financeiro_safra")
    )
    private Safra safra;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public LancamentoFinanceiro() {

    }

    public LancamentoFinanceiro(
        PropriedadeRural propriedade,
        CategoriaFinanceira categoria,
        BigDecimal valor,
        LocalDate data,
        String descricao,
        Safra safra
    ) {
        this.propriedade = propriedade;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.safra = safra;
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
            throw new RegraDeNegocioException("Lançamento financeiro precisa estar ativo para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Lançamento financeiro precisa estar desativado para ativar.");
        }

        ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public PropriedadeRural getPropriedade() {
        return propriedade;
    }

    public CategoriaFinanceira getCategoria() {
        return categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public String getDescricao() {
        return descricao;
    }

    public Safra getSafra() {
        return safra;
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

    public void setCategoria(CategoriaFinanceira categoria) {
        this.categoria = categoria;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setSafra(Safra safra) {
        this.safra = safra;
    }
}
