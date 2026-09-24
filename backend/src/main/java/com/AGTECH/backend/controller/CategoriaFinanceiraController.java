package com.AGTECH.backend.controller;

import com.AGTECH.backend.dtos.CadastroCategoriaFinanceiraRequest;
import com.AGTECH.backend.dtos.CategoriaFinanceiraResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.CategoriaFinanceiraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/categorias-financeiras")
public class CategoriaFinanceiraController {

    private final CategoriaFinanceiraService financeiraService;

    public CategoriaFinanceiraController(
            CategoriaFinanceiraService financeiraService
    ) {
        this.financeiraService = financeiraService;
    }

    @PostMapping
    public ResponseEntity<CategoriaFinanceiraResponse> cadastrar (
            @PathVariable UUID propriedadeId,
            @Valid @RequestBody CadastroCategoriaFinanceiraRequest request,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
            ) {
        CategoriaFinanceiraResponse response = financeiraService.cadastrar(propriedadeId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/categorias-financeiras/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaFinanceiraResponse> atualizar(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @Valid @RequestBody CadastroCategoriaFinanceiraRequest request,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(financeiraService.atualizar(propriedadeId, id, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<CategoriaFinanceiraResponse> desativar(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(financeiraService.desativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<CategoriaFinanceiraResponse> ativar(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(financeiraService.ativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaFinanceiraResponse>> listarPorPropriedade(
            @PathVariable UUID propriedadeId,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(financeiraService.listarPorPropriedade(propriedadeId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaFinanceiraResponse> buscarPorId(
            @PathVariable UUID propriedadeId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(financeiraService.buscarPorId(propriedadeId, id, usuarioDetails.getId()));
    }
}
