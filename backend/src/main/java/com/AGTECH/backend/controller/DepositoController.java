package com.AGTECH.backend.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.CadastroDepositoRequest;
import com.AGTECH.backend.dtos.DepositoResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.DepositoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/depositos")
public class DepositoController {
    
    private final DepositoService depositoService;
    
    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @PostMapping
    public ResponseEntity<DepositoResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @Valid @RequestBody CadastroDepositoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        DepositoResponse response = depositoService.cadastrar(propriedadeId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/"+ propriedadeId + "/depositos/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepositoResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroDepositoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(depositoService.atualizar(propriedadeId, id, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<DepositoResponse> desativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(depositoService.desativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<DepositoResponse> ativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(depositoService.ativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<DepositoResponse>> listarPorPropriedade(
        @PathVariable UUID propriedadeId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(depositoService.listarPorPropriedade(propriedadeId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositoResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(depositoService.buscarPorId(propriedadeId, id, usuarioDetails.getId()));
    }
}
