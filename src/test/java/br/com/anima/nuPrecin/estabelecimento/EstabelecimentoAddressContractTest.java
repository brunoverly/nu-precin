package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.endereco.EnderecoRepository;
import br.com.anima.nuPrecin.storage.ImageStorageService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EstabelecimentoAddressContractTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private EnderecoRepository enderecos;
    @MockitoBean private ImageStorageService images;

    @Test
    void shouldReturnFullAddressAcrossAllEstablishmentEndpoints() throws Exception {
        Usuario usuario = usuarios.save(Usuario.builder()
                .nome("Maria").email("endereco@example.com").senha("hash").build());
        String payload = """
                {"tipo":"SUPERMERCADO","nome":"Mercado contrato endereço","telefone":"3133334455",
                 "idUsuario":%d,"endereco":{"logradouro":"Avenida Antônio Carlos","numero":"4500",
                 "bairro":"Pampulha","cidade":"Belo Horizonte","estado":"MG","cep":"31270010"}}
                """.formatted(usuario.getId());
        var created = mvc.perform(post("/v1/estabelecimentos").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endereco.numero").value("4500"))
                .andExpect(jsonPath("$.endereco.cep").value("31270010"))
                .andExpect(jsonPath("$.endereco.latitude").value(nullValue()))
                .andExpect(jsonPath("$.endereco.longitude").value(nullValue()))
                .andReturn();
        var body = json.readTree(created.getResponse().getContentAsString());
        long id = body.get("id").asLong();
        long enderecoId = body.get("idEndereco").asLong();
        assertEquals(enderecoId, body.path("endereco").path("id").asLong());

        mvc.perform(get("/v1/estabelecimentos/{id}", id).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.logradouro").value("Avenida Antônio Carlos"))
                .andExpect(jsonPath("$.endereco.bairro").value("Pampulha"))
                .andExpect(jsonPath("$.endereco.cidade").value("Belo Horizonte"))
                .andExpect(jsonPath("$.endereco.estado").value("MG"));
        mvc.perform(get("/v1/estabelecimentos").param("nome", "Mercado contrato endereço").with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].endereco.id").value(enderecoId));

        var endereco = enderecos.findById(enderecoId).orElseThrow();
        endereco.setLatitude(-19.86);
        endereco.setLongitude(-43.96);
        enderecos.saveAndFlush(endereco);
        mvc.perform(put("/v1/estabelecimentos/{id}", id).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.latitude").value(-19.86));
        mvc.perform(put("/v1/estabelecimentos/{id}", id).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload.replace("4500", "4501")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEndereco").value(enderecoId))
                .andExpect(jsonPath("$.endereco.numero").value("4501"))
                .andExpect(jsonPath("$.endereco.latitude").value(nullValue()))
                .andExpect(jsonPath("$.endereco.longitude").value(nullValue()));
        assertEquals("4501", enderecos.findById(enderecoId).orElseThrow().getNumero());

        when(images.upload(eq("estabelecimentos"), eq(id), eq("fotos"), any()))
                .thenReturn("https://example.com/foto.jpg");
        mvc.perform(multipart("/v1/estabelecimentos/{id}/foto", id).file(
                        new MockMultipartFile("file", "foto.png", "image/png", new byte[]{1})).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.id").value(enderecoId))
                .andExpect(jsonPath("$.endereco.numero").value("4501"));

        String legacyPayload = """
                {"tipo":"SUPERMERCADO","nome":"Mercado legado","telefone":"3133334455",
                 "idUsuario":%d,"idEndereco":%d}
                """.formatted(usuario.getId(), enderecoId);
        mvc.perform(put("/v1/estabelecimentos/{id}", id).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(legacyPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.id").value(enderecoId));
        mvc.perform(put("/v1/estabelecimentos/{id}", id).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload.replace("31270010", "123")))
                .andExpect(status().isBadRequest());

        var legacyEmbedded = json.readTree(payload);
        var legacyAddress = (com.fasterxml.jackson.databind.node.ObjectNode) legacyEmbedded.get("endereco");
        legacyAddress.remove("numero");
        legacyAddress.remove("cep");
        mvc.perform(post("/v1/estabelecimentos").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(legacyEmbedded)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endereco.numero").value(nullValue()))
                .andExpect(jsonPath("$.endereco.cep").value(nullValue()));
    }
}
