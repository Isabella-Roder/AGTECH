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

import com.AGTECH.backend.dtos.AnimalResponse;
import com.AGTECH.backend.dtos.CadastroAnimalRequest;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.AnimalService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/rebanhos/{rebanhoId}/animais")
public class AnimalController {
    
    private final AnimalService animalService;

    public AnimalController(
        AnimalService animalService
    ) {
        this.animalService = animalService;
    }

    @PostMapping 
    public ResponseEntity<AnimalResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID rebanhoId,
        @Valid @RequestBody CadastroAnimalRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        AnimalResponse response = animalService.cadastrar(propriedadeId, rebanhoId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/rebanhos/" + rebanhoId + "/animais/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID rebanhoId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroAnimalRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(animalService.atualizar(propriedadeId, rebanhoId, id, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<AnimalResponse> desativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID rebanhoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(animalService.desativar(propriedadeId, rebanhoId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<AnimalResponse> ativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID rebanhoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(animalService.ativar(propriedadeId, rebanhoId, id, usuarioDetails.getId()));
    }

    @GetMapping 
    public ResponseEntity<List<AnimalResponse>> listarPorRebanho(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID rebanhoId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(animalService.listarPorRebanho(propriedadeId, rebanhoId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID rebanhoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(animalService.buscarPorId(propriedadeId, rebanhoId, id, usuarioDetails.getId()));
    }
}
