package com.AGTECH.backend.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.AtividadeDeCampoResponse;
import com.AGTECH.backend.dtos.CadastroAtividadeDeCampoRequest;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.AtividadeDeCampoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras/{safraId}/atividades")
public class AtividadeDeCampoController {
    
    private final AtividadeDeCampoService atividadeDeCampoService;

    public AtividadeDeCampoController(
        AtividadeDeCampoService atividadeDeCampoService
    ) {
        this.atividadeDeCampoService = atividadeDeCampoService;
    }

    @PostMapping
    public ResponseEntity<AtividadeDeCampoResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @Valid @RequestBody CadastroAtividadeDeCampoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        AtividadeDeCampoResponse response = atividadeDeCampoService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/"+ propriedadeId + "/talhoes/" + talhaoId + "/safras/" + safraId + "/atividades/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AtividadeDeCampoResponse>> listarPorSafraId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(atividadeDeCampoService.listarPorSafra(propriedadeId, talhaoId, safraId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeDeCampoResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(atividadeDeCampoService.buscarPorId(propriedadeId, talhaoId, safraId, id, usuarioDetails.getId()));
    }
}