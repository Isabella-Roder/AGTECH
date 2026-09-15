package com.AGTECH.backend.models;

import com.AGTECH.backend.enums.TipoManutencao;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "manutencoes",
        indexes = {
                @Index(
                        name = "idx_manutencao_maquina",
                        columnList = "fk_manutencao_maquina"
                )
        }
)
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fk_manutencao_maquina",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_manutencao_maquina")
    )
    private Maquina maquina;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoManutencao tipo;

    @Column(name = "data_realizacao", nullable = false)
    private LocalDate dataRealizacao;

    @Column(name = "horimetro_no_momento", nullable = false)
    private Double horimetroNoMomento;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Manutencao() {

    }

    public Manutencao(
            Maquina maquina,
            TipoManutencao tipo,
            LocalDate dataRealizacao,
            Double horimetroNoMomento,
            String descricao
    ) {
        this.maquina = maquina;
        this.tipo = tipo;
        this.dataRealizacao = dataRealizacao;
        this.horimetroNoMomento = horimetroNoMomento;
        this.descricao = descricao;
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

    public TipoManutencao getTipo() {
        return tipo;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public Double getHorimetroNoMomento() {
        return horimetroNoMomento;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setTipo(TipoManutencao tipo) {
        this.tipo = tipo;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public void setHorimetroNoMomento(Double horimetroNoMomento) {
        this.horimetroNoMomento = horimetroNoMomento;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
