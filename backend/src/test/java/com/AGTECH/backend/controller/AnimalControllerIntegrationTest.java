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
import org.springframework.http.MediaType;

import com.jayway.jsonpath.JsonPath;

class AnimalControllerIntegrationTest extends IntegracaoApiTestBase {

    private static final String CORPO_VALIDO =
            "{\"identificacao\":\"Brinco 01\",\"sexo\":\"FEMEA\",\"dataNascimento\":\"2023-01-15\"}";

    private String criarRebanho(String token, String propriedadeId) throws Exception {
        String resposta = mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/rebanhos")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Gado de corte\",\"especie\":\"BOVINO\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    private String urlAnimais(String propriedadeId, String rebanhoId) {
        return "/api/propriedades/" + propriedadeId + "/rebanhos/" + rebanhoId + "/animais";
    }

    private String criarAnimal(String token, String propriedadeId, String rebanhoId) throws Exception {
        String resposta = mockMvc.perform(post(urlAnimais(propriedadeId, rebanhoId))
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
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(post(urlAnimais(propriedadeId, rebanhoId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.startsWith(urlAnimais(propriedadeId, rebanhoId) + "/")))
                .andExpect(jsonPath("$.identificacao").value("Brinco 01"))
                .andExpect(jsonPath("$.sexo").value("FEMEA"))
                .andExpect(jsonPath("$.dataNascimento").value("2023-01-15"))
                .andExpect(jsonPath("$.rebanhoId").value(rebanhoId))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void cadastrar_semDataNascimento_deveRetornar201() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(post(urlAnimais(propriedadeId, rebanhoId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identificacao\":\"Brinco 02\",\"sexo\":\"MACHO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataNascimento").doesNotExist());
    }

    @Test
    void cadastrar_semIdentificacao_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(post(urlAnimais(propriedadeId, rebanhoId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sexo\":\"FEMEA\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comSexoInvalido_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(post(urlAnimais(propriedadeId, rebanhoId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identificacao\":\"Brinco 01\",\"sexo\":\"OUTRO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comDataNascimentoNoFuturo_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);

        mockMvc.perform(post(urlAnimais(propriedadeId, rebanhoId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identificacao\":\"Brinco 01\",\"sexo\":\"FEMEA\",\"dataNascimento\":\"2999-01-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_semToken_deveSerBloqueado() throws Exception {
        mockMvc.perform(post(urlAnimais(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarEBuscarPorId_deveRetornar200() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);
        String animalId = criarAnimal(token, propriedadeId, rebanhoId);

        mockMvc.perform(get(urlAnimais(propriedadeId, rebanhoId)).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(animalId));

        mockMvc.perform(get(urlAnimais(propriedadeId, rebanhoId) + "/" + animalId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(animalId));
    }

    @Test
    void atualizar_deveAlterarCampos() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);
        String animalId = criarAnimal(token, propriedadeId, rebanhoId);

        mockMvc.perform(put(urlAnimais(propriedadeId, rebanhoId) + "/" + animalId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"identificacao\":\"Brinco 99\",\"sexo\":\"MACHO\",\"dataNascimento\":\"2022-05-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificacao").value("Brinco 99"))
                .andExpect(jsonPath("$.sexo").value("MACHO"))
                .andExpect(jsonPath("$.dataNascimento").value("2022-05-10"));
    }

    @Test
    void desativarEAtivar_deveAlternarStatus() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanhoId = criarRebanho(token, propriedadeId);
        String animalId = criarAnimal(token, propriedadeId, rebanhoId);
        String base = urlAnimais(propriedadeId, rebanhoId) + "/" + animalId;

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
        String animalId = criarAnimal(token, propriedadeId, rebanhoId);
        String base = urlAnimais(propriedadeId, rebanhoId) + "/" + animalId;

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void acessarPropriedadeDeOutroUsuario_deveRetornar403() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String rebanhoA = criarRebanho(tokenA, propriedadeA);
        criarAnimal(tokenA, propriedadeA, rebanhoA);

        String tokenB = cadastrarUsuarioELogar();

        mockMvc.perform(get(urlAnimais(propriedadeA, rebanhoA)).header("Authorization", tokenB))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(urlAnimais(propriedadeA, rebanhoA))
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void usarRebanhoDeOutraPropriedadeNaUrlPropria_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String rebanhoA = criarRebanho(tokenA, propriedadeA);
        String animalA = criarAnimal(tokenA, propriedadeA, rebanhoA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);
        String urlCruzada = urlAnimais(propriedadeB, rebanhoA);

        mockMvc.perform(get(urlCruzada).header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(post(urlCruzada)
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(get(urlCruzada + "/" + animalA).header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(patch(urlCruzada + "/" + animalA + "/desativar").header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void usarAnimalDeOutroRebanhoDaMesmaPropriedade_deveRetornar422() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String rebanho1 = criarRebanho(token, propriedadeId);
        String rebanho2 = criarRebanho(token, propriedadeId);
        String animalDoRebanho1 = criarAnimal(token, propriedadeId, rebanho1);

        String urlRebanhoErrado = urlAnimais(propriedadeId, rebanho2) + "/" + animalDoRebanho1;

        mockMvc.perform(get(urlRebanhoErrado).header("Authorization", token))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(put(urlRebanhoErrado)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isUnprocessableContent());
    }
}
