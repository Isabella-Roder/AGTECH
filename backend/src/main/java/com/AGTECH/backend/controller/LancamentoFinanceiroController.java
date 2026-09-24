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

import com.AGTECH.backend.dtos.CadastroLancamentoFinanceiroRequest;
import com.AGTECH.backend.dtos.LancamentoFinanceiroResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.LancamentoFinanceiroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/lancamentos-financeiros")
public class LancamentoFinanceiroController {

    private final LancamentoFinanceiroService lancamentoFinanceiroService;

    public LancamentoFinanceiroController(
        LancamentoFinanceiroService lancamentoFinanceiroService
    ) {
        this.lancamentoFinanceiroService = lancamentoFinanceiroService;
    }

    @PostMapping
    public ResponseEntity<LancamentoFinanceiroResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @Valid @RequestBody CadastroLancamentoFinanceiroRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        LancamentoFinanceiroResponse response = lancamentoFinanceiroService.cadastrar(propriedadeId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/lancamentos-financeiros/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LancamentoFinanceiroResponse> atualizar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @Valid @RequestBody CadastroLancamentoFinanceiroRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(lancamentoFinanceiroService.atualizar(propriedadeId, id, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<LancamentoFinanceiroResponse> desativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(lancamentoFinanceiroService.desativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<LancamentoFinanceiroResponse> ativar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(lancamentoFinanceiroService.ativar(propriedadeId, id, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<LancamentoFinanceiroResponse>> listarPorPropriedade(
        @PathVariable UUID propriedadeId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(lancamentoFinanceiroService.listarPorPropriedade(propriedadeId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoFinanceiroResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(lancamentoFinanceiroService.buscarPorId(propriedadeId, id, usuarioDetails.getId()));
    }
}
