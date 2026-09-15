package com.AGTECH.backend.controller;

import com.AGTECH.backend.dtos.CadastroMaquinaRequest;
import com.AGTECH.backend.dtos.MaquinaResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.MaquinaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/maquinas")
public class MaquinaController {

    private final MaquinaService maquinaService;

    public MaquinaController(
            MaquinaService maquinaService
    ) {
        this.maquinaService = maquinaService;
    }

    @PostMapping
    public ResponseEntity<MaquinaResponse> cadastrar(
            @PathVariable UUID propriedadeId,
            @Valid @RequestBody CadastroMaquinaRequest request,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
            ) {
        MaquinaResponse response = maquinaService.cadastrar(propriedadeId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/maquinas/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<MaquinaResponse> desativar(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(maquinaService.desativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<MaquinaResponse> ativar(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(maquinaService.ativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<MaquinaResponse>> listarPorPropriedade(
            @PathVariable UUID propriedadeId,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(maquinaService.listarPorPropriedade(propriedadeId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaResponse> buscarPorId(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(maquinaService.buscarPorId(propriedadeId, id, usuarioDetails.getId()));
    }
}
