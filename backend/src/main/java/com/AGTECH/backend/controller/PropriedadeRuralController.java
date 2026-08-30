package com.AGTECH.backend.controller;

import java.net.URI;
import java.util.List;

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

import com.AGTECH.backend.dtos.CadastroPropriedadeRequest;
import com.AGTECH.backend.dtos.PropriedadeResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.PropriedadeRuralService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propriedades")
public class PropriedadeRuralController {
    
    private final PropriedadeRuralService propriedadeRuralService;

    public PropriedadeRuralController(
        PropriedadeRuralService propriedadeRuralService
    ) {
        this.propriedadeRuralService = propriedadeRuralService;
    }

    @PostMapping
    public ResponseEntity<PropriedadeResponse> cadastrar(
        @Valid @RequestBody CadastroPropriedadeRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        PropriedadeResponse response = propriedadeRuralService.cadastrar(request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropriedadeResponse> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody CadastroPropriedadeRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(propriedadeRuralService.atualizar(id, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<PropriedadeResponse> desativar(
        @PathVariable Long id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(propriedadeRuralService.desativar(id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<PropriedadeResponse> ativar(
        @PathVariable Long id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(propriedadeRuralService.ativar(id, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropriedadeResponse> buscarPorId(
        @PathVariable Long id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(propriedadeRuralService.buscarPorId(id, usuarioDetails.getId()));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<PropriedadeResponse>> listarMinhas(
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(propriedadeRuralService.listarMinhas(usuarioDetails.getId()));
    }
}
