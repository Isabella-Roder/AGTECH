package com.AGTECH.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
abstract class IntegracaoApiTestBase {

    private static final String SENHA = "12345678";

    @Autowired
    protected MockMvc mockMvc;

    protected String cadastrarUsuarioELogar() throws Exception {
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

    protected String criarPropriedade(String token) throws Exception {
        String resposta = mockMvc.perform(post("/api/propriedades")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Fazenda Teste\",\"municipio\":\"Uberlandia\",\"estado\":\"MG\",\"areaTotalHectares\":50.0}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }
}
