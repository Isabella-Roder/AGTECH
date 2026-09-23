package com.AGTECH.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RebanhoControllerIntegrationTest {

    private static final String SENHA = "12345678";

    @Autowired private MockMvc mockMvc;

    private String cadastrarUsuarioELogar() throws Exception {
        String email = "usuario-" + UUID.randomUUID() + "@agtech.com";

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Usuario Teste\",\"email\":\"" + email + "\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isCreated());

        String resposta = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return "Bearer " + JsonPath.read(resposta, "$.token");
    }

    private String criarPropriedade(String token) throws Exception {
        String resposta = mockMvc.perform(post("/api/propriedades")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Fazenda Teste\",\"municipio\":\"Uberlandia\",\"estado\":\"MG\",\"areaTotalHectares\":50.0}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    private String criarRebanho(String token, String propriedadeId) throws Exception {
        String resposta = mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/rebanhos")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Gado de corte\",\"especie\":\"BOVINO\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void cadastrar_deveRetornar201ComLocation() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/rebanhos")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Gado de corte\",\"especie\":\"BOVINO\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.startsWith("/api/propriedades/" + propriedadeId + "/rebanhos/")))
                .andExpect(jsonPath("$.nome").value("Gado de corte"))
                .andExpect(jsonPath("$.especie").value("BOVINO"))
                .andExpect(jsonPath("$.propriedadeId").value(propriedadeId))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void cadastrar_semNome_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/rebanhos")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"especie\":\"BOVINO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comEspecieInvalida_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/rebanhos")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Gado\",\"especie\":\"DRAGAO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_semToken_deveSerBloqueado() throws Exception {
        mockMvc.perform(post("/api/propriedades/" + UUID.randomUUID() + "/rebanhos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Gado\",\"especie\":\"BOVINO\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarEBuscarPorId_deveRetornar200() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(get("/api/propriedades/" + propriedadeId + "/rebanhos")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(rebanhoId));

        mockMvc.perform(get("/api/propriedades/" + propriedadeId + "/rebanhos/" + rebanhoId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rebanhoId));
    }

    @Test
    void atualizar_deveAlterarNomeEEspecie() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(put("/api/propriedades/" + propriedadeId + "/rebanhos/" + rebanhoId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Ovelhas\",\"especie\":\"OVINO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ovelhas"))
                .andExpect(jsonPath("$.especie").value("OVINO"));
    }

    @Test
    void desativarEAtivar_deveAlternarStatus() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);
        String base = "/api/propriedades/" + propriedadeId + "/rebanhos/" + rebanhoId;

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
        String rebanhoId = criarRebanho(token, propriedadeId);
        String base = "/api/propriedades/" + propriedadeId + "/rebanhos/" + rebanhoId;

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void acessarPropriedadeDeOutroUsuario_deveRetornar403() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        criarRebanho(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();

        mockMvc.perform(get("/api/propriedades/" + propriedadeA + "/rebanhos")
                .header("Authorization", tokenB))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/propriedades/" + propriedadeA + "/rebanhos")
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Invasor\",\"especie\":\"BOVINO\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void usarRebanhoDeOutraPropriedadeNaUrlPropria_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String rebanhoA = criarRebanho(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);
        String urlCruzada = "/api/propriedades/" + propriedadeB + "/rebanhos/" + rebanhoA;

        mockMvc.perform(get(urlCruzada).header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(put(urlCruzada)
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Roubado\",\"especie\":\"BOVINO\"}"))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(patch(urlCruzada + "/desativar").header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());
    }
}
