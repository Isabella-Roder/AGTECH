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

class CategoriaFinanceiraControllerIntegrationTest extends IntegracaoApiTestBase {

    private static final String CORPO_VALIDO = "{\"nome\":\"Sementes\",\"tipo\":\"DESPESA\"}";

    private String url(String propriedadeId) {
        return "/api/propriedades/" + propriedadeId + "/categorias-financeiras";
    }

    private String criarCategoria(String token, String propriedadeId) throws Exception {
        String resposta = mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    private void criarLancamento(String token, String propriedadeId, String categoriaId) throws Exception {
        mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/lancamentos-financeiros")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":\"" + categoriaId + "\",\"valor\":100.00,\"data\":\"2026-03-10\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrar_deveRetornar201ComLocation() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.startsWith(url(propriedadeId) + "/")))
                .andExpect(jsonPath("$.nome").value("Sementes"))
                .andExpect(jsonPath("$.tipo").value("DESPESA"))
                .andExpect(jsonPath("$.propriedadeId").value(propriedadeId))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void cadastrar_semNome_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"DESPESA\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comTipoInvalido_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Sementes\",\"tipo\":\"EMPRESTIMO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_semToken_deveSerBloqueado() throws Exception {
        mockMvc.perform(post(url(UUID.randomUUID().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarEBuscarPorId_deveRetornar200() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);

        mockMvc.perform(get(url(propriedadeId)).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(categoriaId));

        mockMvc.perform(get(url(propriedadeId) + "/" + categoriaId).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoriaId));
    }

    @Test
    void atualizar_semLancamentos_devePermitirTrocarTipo() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);

        mockMvc.perform(put(url(propriedadeId) + "/" + categoriaId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Venda de soja\",\"tipo\":\"RECEITA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Venda de soja"))
                .andExpect(jsonPath("$.tipo").value("RECEITA"));
    }

    @Test
    void atualizar_comLancamentos_naoDevePermitirTrocarTipo() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);
        criarLancamento(token, propriedadeId, categoriaId);

        mockMvc.perform(put(url(propriedadeId) + "/" + categoriaId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Sementes\",\"tipo\":\"RECEITA\"}"))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(put(url(propriedadeId) + "/" + categoriaId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Adubos\",\"tipo\":\"DESPESA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Adubos"));
    }

    @Test
    void desativarEAtivar_deveAlternarStatus() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String base = url(propriedadeId) + "/" + criarCategoria(token, propriedadeId);

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
        String base = url(propriedadeId) + "/" + criarCategoria(token, propriedadeId);

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void acessarPropriedadeDeOutroUsuario_deveRetornar403() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        criarCategoria(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();

        mockMvc.perform(get(url(propriedadeA)).header("Authorization", tokenB))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(url(propriedadeA))
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void usarCategoriaDeOutraPropriedadeNaUrlPropria_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String categoriaA = criarCategoria(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);
        String urlCruzada = url(propriedadeB) + "/" + categoriaA;

        mockMvc.perform(get(urlCruzada).header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(put(urlCruzada)
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CORPO_VALIDO))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(patch(urlCruzada + "/desativar").header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());
    }
}
