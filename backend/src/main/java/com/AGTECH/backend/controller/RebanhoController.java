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

import com.AGTECH.backend.dtos.CadastroRebanhoRequest;
import com.AGTECH.backend.dtos.RebanhoResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.RebanhoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/rebanhos")
public class RebanhoController {
    
    private final RebanhoService rebanhoService;

    public RebanhoController(
        RebanhoService rebanhoService
    ) {
        this.rebanhoService = rebanhoService;
    }

    @PostMapping 
    public ResponseEntity<RebanhoResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @Valid @RequestBody CadastroRebanhoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        RebanhoResponse response = rebanhoService.cadastrar(propriedadeId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/rebanhos/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RebanhoResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroRebanhoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(rebanhoService.atualizar(propriedadeId, id, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<RebanhoResponse> desativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(rebanhoService.desativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<RebanhoResponse> ativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(rebanhoService.ativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<RebanhoResponse>> listarPorPropriedade(
        @PathVariable UUID propriedadeId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(rebanhoService.listarPorPropriedade(propriedadeId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RebanhoResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(rebanhoService.buscarPorId(propriedadeId, id, usuarioDetails.getId()));
    }
}
