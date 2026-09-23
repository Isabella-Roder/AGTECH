package com.AGTECH.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.jayway.jsonpath.JsonPath;

class MaquinaControllerIntegrationTest extends IntegracaoApiTestBase {

    private static final String CORPO_VALIDO =
            "{\"identificador\":\"Trator 01\",\"tipo\":\"TRATOR\",\"horimetroAtual\":100.0}";

    private String criarMaquina(String token, String propriedadeId) throws Exception {
        String resposta = mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/maquinas")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void cadastrar_deveRetornar201ComLocation() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/maquinas")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.startsWith("/api/propriedades/" + propriedadeId + "/maquinas/")))
                .andExpect(jsonPath("$.identificador").value("Trator 01"))
                .andExpect(jsonPath("$.tipo").value("TRATOR"))
                .andExpect(jsonPath("$.horimetroAtual").value(100.0))
                .andExpect(jsonPath("$.propriedadeId").value(propriedadeId))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void cadastrar_semIdentificador_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/maquinas")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"TRATOR\",\"horimetroAtual\":100.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comTipoInvalido_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/maquinas")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identificador\":\"X\",\"tipo\":\"FOGUETE\",\"horimetroAtual\":100.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comHorimetroNegativo_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/maquinas")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identificador\":\"X\",\"tipo\":\"TRATOR\",\"horimetroAtual\":-5.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_semToken_deveSerBloqueado() throws Exception {
        mockMvc.perform(post("/api/propriedades/" + UUID.randomUUID() + "/maquinas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarEBuscarPorId_deveRetornar200() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String maquinaId = criarMaquina(token, propriedadeId);

        mockMvc.perform(get("/api/propriedades/" + propriedadeId + "/maquinas")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(maquinaId));

        mockMvc.perform(get("/api/propriedades/" + propriedadeId + "/maquinas/" + maquinaId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(maquinaId));
    }

    @Test
    void desativarEAtivar_deveAlternarStatus() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String maquinaId = criarMaquina(token, propriedadeId);
        String base = "/api/propriedades/" + propriedadeId + "/maquinas/" + maquinaId;

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));

        mockMvc.perform(patch(base + "/ativar").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void desativar_duasVezes_deveRetornar422() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String maquinaId = criarMaquina(token, propriedadeId);
        String base = "/api/propriedades/" + propriedadeId + "/maquinas/" + maquinaId;

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void acessarPropriedadeDeOutroUsuario_deveRetornar403() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        criarMaquina(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();

        mockMvc.perform(get("/api/propriedades/" + propriedadeA + "/maquinas")
                .header("Authorization", tokenB))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/propriedades/" + propriedadeA + "/maquinas")
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void usarMaquinaDeOutraPropriedadeNaUrlPropria_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String maquinaA = criarMaquina(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);
        String urlCruzada = "/api/propriedades/" + propriedadeB + "/maquinas/" + maquinaA;

        mockMvc.perform(get(urlCruzada).header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(patch(urlCruzada + "/desativar").header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());
    }
}
