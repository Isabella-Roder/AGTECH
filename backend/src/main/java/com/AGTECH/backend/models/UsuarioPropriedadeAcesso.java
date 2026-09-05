package com.AGTECH.backend.models;

import java.util.UUID;

import com.AGTECH.backend.enums.PapelAcesso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "usuario_propriedade_acesso",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "propriedade_id"})
)
public class UsuarioPropriedadeAcesso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private PropriedadeRural propriedade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PapelAcesso papel;

    public UsuarioPropriedadeAcesso() {
        
    }

    public UsuarioPropriedadeAcesso(
        Usuario usuario,
        PropriedadeRural propriedade,
        PapelAcesso papel
    ) {
        this.usuario = usuario;
        this.propriedade = propriedade;
        this.papel = papel;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public PropriedadeRural getPropriedade() {
        return propriedade;
    }

    public PapelAcesso getPapel() {
        return papel;
    }
}
