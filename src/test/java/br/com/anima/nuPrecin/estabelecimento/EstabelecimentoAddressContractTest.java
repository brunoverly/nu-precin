package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.endereco.EnderecoRepository;
import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.endereco.GeoapifyGeocodingClient;
import br.com.anima.nuPrecin.endereco.EnderecoGeocodingService;
import br.com.anima.nuPrecin.endereco.GeocodingBackfillJob;
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
import java.util.Optional;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
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
    @Autowired private EstabelecimentoRepository estabelecimentos;
    @Autowired private EnderecoGeocodingService geocodingService;
    @MockitoBean private GeoapifyGeocodingClient geocoding;
    @MockitoBean private ImageStorageService images;

    @Test
    void shouldGeocodeOnCreateAndAddressChangesAndAllowOwnerToRetryFailures() throws Exception {
        Usuario usuario = usuarios.save(Usuario.builder()
                .nome("Maria").email("geocoding@example.com").senha("hash").build());
        String payload = """
                {"tipo":"SUPERMERCADO","nome":"Mercado","telefone":"3133334455",
                 "idUsuario":%d,"endereco":{"logradouro":"Rua A","numero":"10",
                 "bairro":"Centro","cidade":"Belo Horizonte","estado":"MG"}}
                """.formatted(usuario.getId());
        when(geocoding.isConfigured()).thenReturn(true);
        when(geocoding.geocode(any())).thenReturn(Optional.of(new GeoapifyGeocodingClient.Coordinates(-19.9, -43.9)));
        var response = mvc.perform(post("/v1/estabelecimentos").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endereco.latitude").value(-19.9))
                .andReturn();
        long id = json.readTree(response.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(put("/v1/estabelecimentos/{id}", id).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
        verify(geocoding, times(1)).geocode(any());

        when(geocoding.geocode(any())).thenReturn(Optional.empty());
        mvc.perform(put("/v1/estabelecimentos/{id}", id).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(payload.replace("Rua A", "Rua B")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.latitude").value(nullValue()));
        var endereco = estabelecimentos.findById(id).orElseThrow().getEndereco();
        assertNotNull(endereco.getGeocodificacaoTentadaEm());

        Usuario outro = usuarios.save(Usuario.builder().nome("Outro").email("outro@example.com").senha("hash").build());
        mvc.perform(post("/v1/estabelecimentos/{id}/geocodificar", id).with(user(outro)))
                .andExpect(status().isForbidden());

        when(geocoding.geocode(any())).thenReturn(Optional.of(new GeoapifyGeocodingClient.Coordinates(-19.91, -43.91)));
        mvc.perform(post("/v1/estabelecimentos/{id}/geocodificar", id).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.latitude").value(-19.91))
                .andExpect(jsonPath("$.endereco.longitude").value(-43.91));
    }

    @Test
    void shouldBackfillOldAddressesAndWaitBeforeRepeatingFailedAttempts() {
        Usuario usuario = usuarios.save(Usuario.builder().nome("Maria").email("batch@example.com").senha("hash").build());
        var antigo = salvarEstabelecimento(usuario, "Antigo", null, null, true).getEndereco();
        var recente = salvarEstabelecimento(usuario, "Tentado recentemente", null, null, true).getEndereco();
        recente.setGeocodificacaoTentadaEm(LocalDateTime.now());
        enderecos.saveAndFlush(recente);
        when(geocoding.isConfigured()).thenReturn(true);
        when(geocoding.geocode(any())).thenReturn(Optional.empty());
        var job = new GeocodingBackfillJob(geocoding, enderecos, geocodingService);
        job.reprocessarPendentes();
        verify(geocoding, times(1)).geocode(any());
        assertNotNull(antigo.getGeocodificacaoTentadaEm());
        job.reprocessarPendentes();
        verify(geocoding, times(1)).geocode(any());
        antigo.setGeocodificacaoTentadaEm(LocalDateTime.now().minusHours(7));
        enderecos.saveAndFlush(antigo);
        when(geocoding.geocode(any())).thenReturn(Optional.of(new GeoapifyGeocodingClient.Coordinates(-19.9, -43.9)));
        job.reprocessarPendentes();
        assertEquals(-19.9, antigo.getLatitude());
        assertEquals(-43.9, antigo.getLongitude());
    }

    @Test
    void shouldFilterAndSortByDistanceAndRejectInvalidCoordinates() throws Exception {
        Usuario usuario = usuarios.save(Usuario.builder().nome("Maria").email("proximos@example.com").senha("hash").build());
        salvarEstabelecimento(usuario, "Segundo", -19.92, -43.93, true);
        salvarEstabelecimento(usuario, "Primeiro", -19.92, -43.94, true);
        salvarEstabelecimento(usuario, "Longe", -19.92, -43.0, true);
        var inativo = salvarEstabelecimento(usuario, "Inativo", -19.92, -43.94, false);
        inativo.setAtivo(false);
        estabelecimentos.saveAndFlush(inativo);
        salvarEstabelecimento(usuario, "Sem coordenadas", null, null, true);
        mvc.perform(get("/v1/estabelecimentos/proximos").with(user(usuario))
                        .param("latitude", "-19.92").param("longitude", "-43.94"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Primeiro"))
                .andExpect(jsonPath("$[0].distanciaKm").value(closeTo(0, 0.0001)))
                .andExpect(jsonPath("$[0].endereco.logradouro").value("Rua A"))
                .andExpect(jsonPath("$[1].nome").value("Segundo"))
                .andExpect(jsonPath("$[1].distanciaKm").value(closeTo(1.045, 0.01)));
        mvc.perform(get("/v1/estabelecimentos/proximos").with(user(usuario))
                        .param("latitude", "-19.92").param("longitude", "-43.94").param("raioKm", "0.5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        for (String latitude : new String[]{"91", "NaN", "Infinity"}) {
            mvc.perform(get("/v1/estabelecimentos/proximos").with(user(usuario))
                            .param("latitude", latitude).param("longitude", "0"))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/v1/estabelecimentos/proximos").with(user(usuario))
                        .param("latitude", "0").param("longitude", "181"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/v1/estabelecimentos/proximos").with(user(usuario))
                        .param("latitude", "0").param("longitude", "0").param("raioKm", "0"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/v1/estabelecimentos/proximos").param("latitude", "0").param("longitude", "0"))
                .andExpect(status().isUnauthorized());
    }

    private Estabelecimento salvarEstabelecimento(Usuario usuario, String nome, Double lat, Double lon, boolean ativo) {
        return estabelecimentos.saveAndFlush(Estabelecimento.builder().nome(nome)
                .tipo(EstabelecimentoTipo.SUPERMERCADO).telefone("3133334455").usuario(usuario).ativo(ativo)
                .endereco(Endereco.builder().logradouro("Rua A").bairro("Centro").cidade("Belo Horizonte")
                        .estado("MG").latitude(lat).longitude(lon).build()).build());
    }

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
