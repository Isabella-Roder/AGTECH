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

import com.AGTECH.backend.dtos.CadastroPlantioRequest;
import com.AGTECH.backend.dtos.PlantioResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.PlantioService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras/{safraId}/plantios")
public class PlantioController {
    
    private final PlantioService plantioService;

    public PlantioController(PlantioService plantioService) {
        this.plantioService = plantioService;
    }

    @PostMapping
    public ResponseEntity<PlantioResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @Valid @RequestBody CadastroPlantioRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        PlantioResponse response = plantioService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/talhoes/" + talhaoId + "/safras/" + safraId + "/plantios/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantioResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroPlantioRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(plantioService.atualizar(propriedadeId, talhaoId, safraId, id, request, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<PlantioResponse>> listar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(plantioService.listar(propriedadeId, safraId, talhaoId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantioResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(plantioService.buscarPorId(propriedadeId, safraId, talhaoId, id, usuarioDetails.getId()));
    }
}
