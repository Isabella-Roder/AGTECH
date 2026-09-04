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

import com.AGTECH.backend.dtos.CadastroTalhaoRequest;
import com.AGTECH.backend.dtos.TalhaoResponse;
import com.AGTECH.backend.security.UsuarioDetails;
import com.AGTECH.backend.service.TalhaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/talhoes")
public class TalhaoController {
    
    private final TalhaoService talhaoService;

    public TalhaoController(TalhaoService talhaoService) {
        this.talhaoService = talhaoService;
    }

    @PostMapping
    public ResponseEntity<TalhaoResponse> cadastrar(
        @PathVariable Long propriedadeId,
        @Valid @RequestBody CadastroTalhaoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        TalhaoResponse response = talhaoService.cadastrar(propriedadeId, request, usuarioDetails.getId());

        URI localizar = URI.create("/api/propriedades/" + propriedadeId + "/talhoes/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{talhaoId}")
    public ResponseEntity<TalhaoResponse> atualizar(
        @PathVariable Long propriedadeId,
        @PathVariable Long talhaoId,
        @Valid @RequestBody CadastroTalhaoRequest request,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(talhaoService.atualizar(propriedadeId, talhaoId, request, usuarioDetails.getId()));
    }

    @PatchMapping("/{talhaoId}/desativar")
    public ResponseEntity<TalhaoResponse> desativar(
        @PathVariable Long propriedadeId,
        @PathVariable Long talhaoId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(talhaoService.desativar(propriedadeId, talhaoId, usuarioDetails.getId()));
    }

    @PatchMapping("/{talhaoId}/ativar")
    public ResponseEntity<TalhaoResponse>  ativar(
        @PathVariable Long propriedadeId,
        @PathVariable Long talhaoId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(talhaoService.ativar(propriedadeId, talhaoId, usuarioDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<TalhaoResponse>> listar(
        @PathVariable Long propriedadeId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(talhaoService.listarPorPropriedade(propriedadeId, usuarioDetails.getId()));
    }

    @GetMapping("/{talhaoId}")
    public ResponseEntity<TalhaoResponse> buscarPorId(
        @PathVariable Long propriedadeId,
        @PathVariable Long talhaoId,
        @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        return ResponseEntity.ok(talhaoService.buscarPorId(propriedadeId, talhaoId, usuarioDetails.getId()));
    }
}
