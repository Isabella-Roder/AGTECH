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

import com.AGTECH.backend.dtos.CadastroManutencaoRequest;
import com.AGTECH.backend.dtos.ManutencaoResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.ManutencaoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/maquinas/{maquinaId}/manutencoes")
public class ManutencaoController {
    
    private final ManutencaoService manutencaoService;

    public ManutencaoController(ManutencaoService manutencaoService) {
        this.manutencaoService = manutencaoService;
    }

    @PostMapping 
    public ResponseEntity<ManutencaoResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @Valid @RequestBody CadastroManutencaoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        ManutencaoResponse response = manutencaoService.cadastrar(propriedadeId, maquinaId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/maquinas/" + maquinaId + "/manutencoes/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManutencaoResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroManutencaoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(manutencaoService.atualizar(propriedadeId, maquinaId, id, request, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<ManutencaoResponse>> listarPorMaquina(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(manutencaoService.listarPorMaquina(propriedadeId, maquinaId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManutencaoResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID maquinaId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(manutencaoService.buscarPorId(propriedadeId, maquinaId, id, usuarioDetails.getId()));
    }
}
