package com.AGTECH.backend.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class TratadorGlobalDeErros {

    private static final Logger LOGGER = LoggerFactory.getLogger(TratadorGlobalDeErros.class);

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroApi> tratarRegraDeNegocio(
            RegraDeNegocioException excecao,
            HttpServletRequest requisicao) {
        return criarResposta(
                HttpStatus.UNPROCESSABLE_CONTENT,
                excecao.getMessage(),
                requisicao.getRequestURI());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroApi> tratarAutenticacao(
            AuthenticationException excecao,
            HttpServletRequest requisicao) {
        return criarResposta(
                HttpStatus.UNAUTHORIZED,
                "Credenciais inválidas.",
                requisicao.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApi> tratarValidacao(
            MethodArgumentNotValidException excecao,
            HttpServletRequest requisicao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                mensagem,
                requisicao.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroApi> tratarErroInesperado(
            Exception excecao,
            HttpServletRequest requisicao) {
        LOGGER.error("Erro inesperado ao processar {} {}",
                requisicao.getMethod(), requisicao.getRequestURI(), excecao);

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                requisicao.getRequestURI());
    }

    private ResponseEntity<ErroApi> criarResposta(
            HttpStatus status,
            String mensagem,
            String caminho) {
        ErroApi erro = new ErroApi(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                caminho);

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AcessoNegadoException.class) 
    public ResponseEntity<ErroApi> tratarAcessoNegado(
        AcessoNegadoException exception,
        HttpServletRequest request
    ) {
        return criarResposta(HttpStatus.FORBIDDEN, exception.getMessage(), request.getRequestURI());
    }
}
