package com.AGTECH.backend.controller;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
public class ControladorParaTest {
    
    @GetMapping("/teste/autenticacao")
    void autenticacao() {
        throw new BadCredentialsException("Usuário inexistente ou senha inválida.");
    }

    @PostMapping("/teste/validacao")
    void validacao(@Valid @RequestBody DtoDeTest dto) {

    }

    record DtoDeTest(@NotBlank(message = "Nome é obrigatório") String nome) {
    
    }
}
