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

import com.AGTECH.backend.dtos.CadastroMovimentacaoEstoqueRequest;
import com.AGTECH.backend.dtos.MovimentacaoEstoqueResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.MovimentacaoEstoqueService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/propriedades/{propriedadeId}/depositos/{depositoId}/movimentacoes")
public class MovimentacaoEstoqueController {
    
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(
        MovimentacaoEstoqueService movimentacaoEstoqueService
    ) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @PostMapping 
    public ResponseEntity<MovimentacaoEstoqueResponse> cadastrar(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID depositoId,
        @Valid @RequestBody CadastroMovimentacaoEstoqueRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        MovimentacaoEstoqueResponse response = movimentacaoEstoqueService.cadastrar(propriedadeId, depositoId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/depositos/" + depositoId + "/movimentacoes/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @GetMapping 
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> listarPorDeposito(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID depositoId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(movimentacaoEstoqueService.listarPorDeposito(propriedadeId, depositoId, usuarioDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEstoqueResponse> buscarPorId(
        @PathVariable UUID propriedadeId,
        @PathVariable UUID depositoId,
        @PathVariable UUID id,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(movimentacaoEstoqueService.buscarPorId(propriedadeId, depositoId, id, usuarioDetails.getId()));
    }
}
