package com.AGTECH.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.AcessoResponse;
import com.AGTECH.backend.dtos.ConcederAcessoRequest;
import com.AGTECH.backend.service.UsuarioPropriedadeAcessoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/acessos")
public class UsuarioPropriedadeAcessoController {
    
    private final UsuarioPropriedadeAcessoService acessoService;

    public UsuarioPropriedadeAcessoController(
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.acessoService = acessoService;
    }

    @PostMapping
    public ResponseEntity<AcessoResponse> conceder(
        @PathVariable Long propriedadeId,
        @Valid @RequestBody ConcederAcessoRequest request
    ) {
        AcessoResponse response = acessoService.conceder(propriedadeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{acessoId}")
    public ResponseEntity<Void> revogar(
        @PathVariable Long propriedadeId,
        @PathVariable Long acessoId
    ) {
        acessoService.revogar(propriedadeId, acessoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AcessoResponse>> listar(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(acessoService.listarPorPropriedade(propriedadeId));
    }
}
