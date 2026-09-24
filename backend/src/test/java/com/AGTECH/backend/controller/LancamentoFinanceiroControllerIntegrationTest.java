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

class LancamentoFinanceiroControllerIntegrationTest extends IntegracaoApiTestBase {

    private String url(String propriedadeId) {
        return "/api/propriedades/" + propriedadeId + "/lancamentos-financeiros";
    }

    private String corpo(String categoriaId, String safraId) {
        String safra = safraId == null ? "" : ",\"safraId\":\"" + safraId + "\"";
        return "{\"categoriaId\":\"" + categoriaId + "\",\"valor\":150.75,\"data\":\"2026-03-10\","
                + "\"descricao\":\"Compra de sementes\"" + safra + "}";
    }

    private String criarCategoria(String token, String propriedadeId) throws Exception {
        String resposta = mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/categorias-financeiras")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Sementes\",\"tipo\":\"DESPESA\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    private String criarSafra(String token, String propriedadeId) throws Exception {
        String talhao = mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/talhoes")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Talhao Norte\",\"areaHectares\":10.0}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String talhaoId = JsonPath.read(talhao, "$.id");

        String cultura = mockMvc.perform(post("/api/culturas")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Soja " + UUID.randomUUID().toString().substring(0, 8) + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String culturaId = JsonPath.read(cultura, "$.id");

        String safra = mockMvc.perform(post("/api/propriedades/" + propriedadeId + "/talhoes/" + talhaoId + "/safras")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"culturaId\":\"" + culturaId + "\",\"nome\":\"Safra 2026\",\"dataFimPrevista\":\"2999-12-31\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(safra, "$.id");
    }

    private String criarLancamento(String token, String propriedadeId, String categoriaId) throws Exception {
        String resposta = mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaId, null)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void cadastrar_semSafra_deveRetornar201ComLocation() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaId, null)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.startsWith(url(propriedadeId) + "/")))
                .andExpect(jsonPath("$.valor").value(150.75))
                .andExpect(jsonPath("$.data").value("2026-03-10"))
                .andExpect(jsonPath("$.categoriaId").value(categoriaId))
                .andExpect(jsonPath("$.propriedadeId").value(propriedadeId))
                .andExpect(jsonPath("$.safraId").doesNotExist())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void cadastrar_comSafraDaMesmaPropriedade_deveRetornar201() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);
        String safraId = criarSafra(token, propriedadeId);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaId, safraId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.safraId").value(safraId));
    }

    @Test
    void cadastrar_comSafraDeOutraPropriedade_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String safraA = criarSafra(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);
        String categoriaB = criarCategoria(tokenB, propriedadeB);

        mockMvc.perform(post(url(propriedadeB))
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaB, safraA)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void cadastrar_comCategoriaDeOutraPropriedade_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String categoriaA = criarCategoria(tokenA, propriedadeA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);

        mockMvc.perform(post(url(propriedadeB))
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaA, null)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void cadastrar_comCategoriaDesativada_deveRetornar422() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);

        mockMvc.perform(patch("/api/propriedades/" + propriedadeId + "/categorias-financeiras/"
                + categoriaId + "/desativar").header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaId, null)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void cadastrar_semCategoria_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"valor\":150.75,\"data\":\"2026-03-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_comValorZeroOuNegativo_deveRetornar400() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":\"" + categoriaId + "\",\"valor\":0,\"data\":\"2026-03-10\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(url(propriedadeId))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":\"" + categoriaId + "\",\"valor\":-5.00,\"data\":\"2026-03-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_semToken_deveSerBloqueado() throws Exception {
        mockMvc.perform(post(url(UUID.randomUUID().toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(UUID.randomUUID().toString(), null)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarEBuscarPorId_deveRetornar200() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);
        String lancamentoId = criarLancamento(token, propriedadeId, categoriaId);

        mockMvc.perform(get(url(propriedadeId)).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(lancamentoId));

        mockMvc.perform(get(url(propriedadeId) + "/" + lancamentoId).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lancamentoId));
    }

    @Test
    void atualizar_deveAlterarCamposETrocarCategoria() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoria1 = criarCategoria(token, propriedadeId);
        String categoria2 = criarCategoria(token, propriedadeId);
        String lancamentoId = criarLancamento(token, propriedadeId, categoria1);

        mockMvc.perform(put(url(propriedadeId) + "/" + lancamentoId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":\"" + categoria2 + "\",\"valor\":200.00,"
                        + "\"data\":\"2026-04-01\",\"descricao\":\"Ajustado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaId").value(categoria2))
                .andExpect(jsonPath("$.valor").value(200.0))
                .andExpect(jsonPath("$.data").value("2026-04-01"))
                .andExpect(jsonPath("$.descricao").value("Ajustado"));
    }

    @Test
    void desativarEAtivar_deveAlternarStatus() throws Exception {
        String token = cadastrarUsuarioELogar();
        String propriedadeId = criarPropriedade(token);
        String categoriaId = criarCategoria(token, propriedadeId);
        String base = url(propriedadeId) + "/" + criarLancamento(token, propriedadeId, categoriaId);

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
        String categoriaId = criarCategoria(token, propriedadeId);
        String base = url(propriedadeId) + "/" + criarLancamento(token, propriedadeId, categoriaId);

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(patch(base + "/desativar").header("Authorization", token))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void acessarPropriedadeDeOutroUsuario_deveRetornar403() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String categoriaA = criarCategoria(tokenA, propriedadeA);
        criarLancamento(tokenA, propriedadeA, categoriaA);

        String tokenB = cadastrarUsuarioELogar();

        mockMvc.perform(get(url(propriedadeA)).header("Authorization", tokenB))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(url(propriedadeA))
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaA, null)))
                .andExpect(status().isForbidden());
    }

    @Test
    void usarLancamentoDeOutraPropriedadeNaUrlPropria_deveRetornar422() throws Exception {
        String tokenA = cadastrarUsuarioELogar();
        String propriedadeA = criarPropriedade(tokenA);
        String categoriaA = criarCategoria(tokenA, propriedadeA);
        String lancamentoA = criarLancamento(tokenA, propriedadeA, categoriaA);

        String tokenB = cadastrarUsuarioELogar();
        String propriedadeB = criarPropriedade(tokenB);
        String categoriaB = criarCategoria(tokenB, propriedadeB);
        String urlCruzada = url(propriedadeB) + "/" + lancamentoA;

        mockMvc.perform(get(urlCruzada).header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(put(urlCruzada)
                .header("Authorization", tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo(categoriaB, null)))
                .andExpect(status().isUnprocessableContent());

        mockMvc.perform(patch(urlCruzada + "/desativar").header("Authorization", tokenB))
                .andExpect(status().isUnprocessableContent());
    }
}
