package com.AGTECH.backend.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.CadastroProdutoRequest;
import com.AGTECH.backend.dtos.ProdutoResponse;
import com.AGTECH.backend.service.ProdutoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(
        @Valid @RequestBody CadastroProdutoRequest request
    ) {
        ProdutoResponse response = produtoService.cadastrar(request);

        URI localizar = URI.create("/api/produtos/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<ProdutoResponse> desativar(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(produtoService.desativar(id));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<ProdutoResponse> ativar(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(produtoService.ativar(id));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        return ResponseEntity.ok(produtoService.listar());
    }
}
