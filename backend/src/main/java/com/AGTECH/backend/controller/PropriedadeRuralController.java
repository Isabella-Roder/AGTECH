package com.AGTECH.backend.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.CadastroPropriedadeRequest;
import com.AGTECH.backend.dtos.PropriedadeResponse;
import com.AGTECH.backend.service.PropriedadeRuralService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propriedades")
public class PropriedadeRuralController {
    
    private final PropriedadeRuralService propriedadeRuralService;

    public PropriedadeRuralController(
        PropriedadeRuralService propriedadeRuralService
    ) {
        this.propriedadeRuralService = propriedadeRuralService;
    }

    @PostMapping
    public ResponseEntity<PropriedadeResponse> cadastrar(
        @Valid @RequestBody CadastroPropriedadeRequest request
    ) {
        PropriedadeResponse response = propriedadeRuralService.cadastrar(request);

        URI localizar = URI.create("/api/propriedades/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropriedadeResponse> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody CadastroPropriedadeRequest request
    ) {
        return ResponseEntity.ok(propriedadeRuralService.atualizar(id, request));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<PropriedadeResponse> desativar(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(propriedadeRuralService.desativar(id));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<PropriedadeResponse> ativar(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(propriedadeRuralService.ativar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropriedadeResponse> buscarPorId(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(propriedadeRuralService.buscarPorId(id));
    }
}
