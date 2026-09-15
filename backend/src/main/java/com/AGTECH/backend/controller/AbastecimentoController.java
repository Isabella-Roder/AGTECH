package com.AGTECH.backend.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.AbastecimentoResponse;
import com.AGTECH.backend.dtos.CadastroAbastecimentoRequest;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.AbastecimentoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/maquinas/{maquinaId}/abastecimentos")
public class AbastecimentoController {
    
    private final AbastecimentoService abastecimentoService;

    public AbastecimentoController(
        AbastecimentoService abastecimentoService
    ) {
        this.abastecimentoService = abastecimentoService;
    }

    @PostMapping 
    public ResponseEntity<AbastecimentoResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @Valid @RequestBody CadastroAbastecimentoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        AbastecimentoResponse response = abastecimentoService.cadastrar(propriedadeId, maquinaId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/maquinas/" + maquinaId + "/abastecimentos/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbastecimentoResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroAbastecimentoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(abastecimentoService.atualizar(propriedadeId, maquinaId, id, request, usuarioDetails.getId()));
    }

    @GetMapping 
    public ResponseEntity<List<AbastecimentoResponse>> listarPorMaquina(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(abastecimentoService.listarPorMaquina(propriedadeId, maquinaId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbastecimentoResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(abastecimentoService.buscarPorId(propriedadeId, maquinaId, id, usuarioDetails.getId()));
    }
}
