package com.AGTECH.backend.exception;

import java.time.Instant;

public record ErroApi(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho) {
}
