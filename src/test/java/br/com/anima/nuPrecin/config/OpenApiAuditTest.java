package br.com.anima.nuPrecin.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiAuditTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @Test
    void documentaStatusReaisEProtegeVinculoGoogle() throws Exception {
        var resposta = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk()).andReturn().getResponse();
        JsonNode api = mapper.readTree(resposta.getContentAsByteArray());
        JsonNode paths = api.path("paths");
        var historico = paths.path("/v1/produtos/{idProduto}/historico-precos").path("get");
        assertTrue(historico.path("responses").has("200"));
        assertEquals("#/components/schemas/ErrorResponse", historico.path("responses").path("404")
                .path("content").path("application/json").path("schema").path("$ref").asText());
        assertTrue(paths.path("/v1/estabelecimentos").path("post").path("responses").has("201"));
        assertTrue(paths.path("/v1/auth/google/vincular").path("post").path("security").path(0).has("bearerAuth"));
    }
}
