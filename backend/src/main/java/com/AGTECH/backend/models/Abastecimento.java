package com.AGTECH.backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "abastecimentos",
        indexes = {
                @Index(name = "idx_abastecimento_maquina",
                    columnList = "fk_abastecimento_maquina"
                )
        }
)
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fk_abastecimento_maquina",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_abastecimento_maquina")
    )
    private Maquina maquina;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "litros", nullable = false)
    private Double litros;

    @Column(name = "horimetro_no_momento", nullable = false)
    private Double horimetroNoMomento;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Abastecimento() {

    }

    public Abastecimento(
            Maquina maquina,
            LocalDateTime data,
            Double litros,
            Double horimetroNoMomento,
            String observacoes
    ) {
        this.maquina = maquina;
        this.data = data;
        this.litros = litros;
        this.horimetroNoMomento = horimetroNoMomento;
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

    public Maquina getMaquina() {
        return maquina;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Double getLitros() {
        return litros;
    }

    public Double getHorimetroNoMomento() {
        return horimetroNoMomento;
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

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setLitros(Double litros) {
        this.litros = litros;
    }

    public void setHorimetroNoMomento(Double horimetroNoMomento) {
        this.horimetroNoMomento = horimetroNoMomento;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
