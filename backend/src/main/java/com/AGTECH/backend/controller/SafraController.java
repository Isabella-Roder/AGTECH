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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.CadastroSafraRequest;
import com.AGTECH.backend.dtos.SafraResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.SafraService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras")
public class SafraController {
    
    private final SafraService safraService;
    
    public SafraController(SafraService safraService) {
        this.safraService = safraService;
    }

    @PostMapping 
    public ResponseEntity<SafraResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @Valid @RequestBody CadastroSafraRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        SafraResponse response = safraService.cadastrar(propriedadeId, talhaoId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/talhoes/" + talhaoId + "/safras/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<SafraResponse> iniciar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(safraService.iniciar(propriedadeId, talhaoId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<SafraResponse> finalizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(safraService.finalizar(propriedadeId, talhaoId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<SafraResponse> cancelar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(safraService.cancelar(propriedadeId, talhaoId, id, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<SafraResponse>> listar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(safraService.listar(propriedadeId, talhaoId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SafraResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(safraService.buscarPorId(propriedadeId, talhaoId, id, usuarioDetails.getId()));
    }
}
