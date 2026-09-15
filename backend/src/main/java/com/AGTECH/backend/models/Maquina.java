package com.AGTECH.backend.models;

import com.AGTECH.backend.enums.TipoMaquina;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maquinas",
    indexes = {
        @Index(name = "idx_propriedade_maquina",
            columnList = "fk_maquina_propriedade"
        )
    }
)
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fk_maquina_propriedade",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_maquina_propriedade")
    )
    private PropriedadeRural propriedade;

    @Column(name = "identificador", nullable = false, length = 100)
    private String identificador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMaquina tipo;

    @Column(name = "horimetro_atual", nullable = false)
    private Double horimetroAtual;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Maquina() {

    }

    public Maquina(
            PropriedadeRural propriedade,
            String identificador,
            TipoMaquina tipo,
            Double horimetroAtual
    ) {
        this.propriedade = propriedade;
        this.identificador = identificador;
        this.tipo = tipo;
        this.horimetroAtual = horimetroAtual;
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
            throw new RegraDeNegocioException("Maquina precisa estar ativa para desativar.");
        }

        ativo = false;
    }

    public void ativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Maquina precisa estar desativada para ativar.");
        }

        ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public PropriedadeRural getPropriedade() {
        return propriedade;
    }

    public String getIdentificador() {
        return identificador;
    }

    public TipoMaquina getTipo() {
        return tipo;
    }

    public Double getHorimetroAtual() {
        return horimetroAtual;
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

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public void setTipo(TipoMaquina tipo) {
        this.tipo = tipo;
    }

    public void setHorimetroAtual(Double horimetroAtual) {
        this.horimetroAtual = horimetroAtual;
    }
}
