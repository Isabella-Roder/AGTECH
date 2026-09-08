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

import com.AGTECH.backend.dtos.CadastroColheitaRequest;
import com.AGTECH.backend.dtos.ColheitaResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.ColheitaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/talhoes/{talhaoId}/safras/{safraId}/colheitas")
public class ColheitaController {

    private final ColheitaService colheitaService;

    public ColheitaController(
        ColheitaService colheitaService
    ) {
        this.colheitaService = colheitaService;
    }

    @PostMapping
    public ResponseEntity<ColheitaResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @Valid @RequestBody CadastroColheitaRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        ColheitaResponse response = colheitaService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/talhoes/" + talhaoId + "/safras/" + safraId + "/colheitas/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ColheitaResponse>> listarPorSafra(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(colheitaService.listarPorSafra(propriedadeId, talhaoId, safraId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColheitaResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID talhaoId,
        @PathVariable UUID safraId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(colheitaService.buscarPorId(propriedadeId, talhaoId, safraId, id, usuarioDetails.getId()));
    }
}
