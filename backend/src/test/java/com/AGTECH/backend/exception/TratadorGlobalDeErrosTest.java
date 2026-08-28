package com.AGTECH.backend.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class TratadorGlobalDeErrosTest {

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ControladorParaTeste())
                .setControllerAdvice(new TratadorGlobalDeErros())
                .build();
    }

    @Test
    void deveRetornarErroPadronizadoParaRegraDeNegocio() throws Exception {
        mockMvc.perform(get("/teste/regra-de-negocio"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.erro").value("Unprocessable Content"))
                .andExpect(jsonPath("$.mensagem").value("Operação não permitida."))
                .andExpect(jsonPath("$.caminho").value("/teste/regra-de-negocio"));
    }

    @Test
    void deveOcultarDetalhesDoErroInesperado() throws Exception {
        mockMvc.perform(get("/teste/erro-interno"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensagem").value("Ocorreu um erro interno inesperado."))
                .andExpect(jsonPath("$.mensagem").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("detalhe sensível"))))
                .andExpect(jsonPath("$.caminho").value("/teste/erro-interno"));
    }

    @RestController
    private static class ControladorParaTeste {

        @GetMapping("/teste/regra-de-negocio")
        void regraDeNegocio() {
            throw new RegraDeNegocioException("Operação não permitida.");
        }

        @GetMapping("/teste/erro-interno")
        void erroInterno() {
            throw new IllegalStateException("detalhe sensível");
        }
    }
}
