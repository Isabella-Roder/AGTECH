package com.AGTECH.backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AGTECH.backend.dtos.CadastroCulturaRequest;
import com.AGTECH.backend.dtos.CulturaResponse;
import com.AGTECH.backend.service.CulturaService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/culturas")
public class CulturaController {
    
    private final CulturaService culturaService;

    public CulturaController(CulturaService culturaService) {
        this.culturaService = culturaService;
    }

    @PostMapping 
    public ResponseEntity<CulturaResponse> cadastrar(
        @Valid @RequestBody CadastroCulturaRequest request
    ) {
        CulturaResponse response = culturaService.cadastrar(request);

        URI localizar = URI.create("/api/culturas/" + response.id());

        return ResponseEntity.created(localizar).body(response);
    }

    @GetMapping 
    public ResponseEntity<List<CulturaResponse>> listar() {
        return ResponseEntity.ok(culturaService.listar());
    }
}
