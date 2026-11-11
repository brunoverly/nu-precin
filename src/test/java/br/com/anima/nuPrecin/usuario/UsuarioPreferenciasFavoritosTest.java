package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoTipo;
import br.com.anima.nuPrecin.security.JwtService;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoEnum;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UsuarioPreferenciasFavoritosTest {
    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private EstabelecimentoRepository estabelecimentos;
    @Autowired private EstabelecimentoFavoritoRepository favoritos;
    @Autowired private ProdutoRepository produtos;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private JwtService jwt;
    @Autowired private EntityManager entityManager;

    private Usuario usuario(String email) {
        return usuarios.saveAndFlush(Usuario.builder().nome("Maria").email(email)
                .senha(encoder.encode("123")).foto("https://example.com/perfil.png").build());
    }

    private String auth(Usuario usuario) {
        return "Bearer " + jwt.generateToken(usuario);
    }

    private Estabelecimento loja(Usuario usuario, String nome, double longitude) {
        return estabelecimentos.saveAndFlush(Estabelecimento.builder().nome(nome)
                .tipo(EstabelecimentoTipo.SUPERMERCADO).usuario(usuario).telefone("3133334455")
                .foto("https://example.com/loja.png")
                .endereco(Endereco.builder().logradouro("Rua A").numero("10").bairro("Centro")
                        .cidade("Belo Horizonte").estado("MG").latitude(0.0).longitude(longitude).build())
                .build());
    }

    @Test
    void shouldPersistEachAllowedRadiusAndReturnItInProfileAndLoginWithoutChangingAccountFields() throws Exception {
        Usuario usuario = usuario("preferencias@example.com");
        Long id = usuario.getId();
        String authorization = auth(usuario);
        String passwordHash = usuario.getSenha();
        mvc.perform(get("/v1/usuarios/{id}/preferencias", id).header("Authorization", authorization))
                .andExpect(status().isOk()).andExpect(jsonPath("$.raioBuscaKm").value(5));

        for (int raio : new int[]{1, 3, 5, 10}) {
            mvc.perform(patch("/v1/usuarios/{id}/preferencias", id).header("Authorization", authorization)
                            .contentType(MediaType.APPLICATION_JSON).content("{\"raioBuscaKm\":" + raio + "}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.raioBuscaKm").value(raio));
            entityManager.flush();
            entityManager.clear();
            assertEquals(raio, usuarios.findById(id).orElseThrow().getRaioBuscaKm());
            mvc.perform(get("/v1/usuarios/{id}/preferencias", id).header("Authorization", authorization))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.raioBuscaKm").value(raio));
            mvc.perform(get("/v1/usuarios/{id}", id).header("Authorization", authorization))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.raioBuscaKm").value(raio));
            mvc.perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"preferencias@example.com\",\"senha\":\"123\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.raioBuscaKm").value(raio));
        }
        Usuario salvo = usuarios.findById(id).orElseThrow();
        assertEquals("Maria", salvo.getNome());
        assertEquals("preferencias@example.com", salvo.getEmail());
        assertEquals(passwordHash, salvo.getSenha());
        assertEquals("https://example.com/perfil.png", salvo.getFoto());
    }

    @Test
    void sessaoContinuaVinculadaAoIdDepoisDaTrocaDeEmail() throws Exception {
        Usuario original = usuario("email-original@example.com");
        String authorization = auth(original);
        original.setEmail("novo-email@example.com");
        usuarios.flush();
        Usuario novoTitular = usuario("email-original@example.com");
        entityManager.clear();

        mvc.perform(get("/v1/usuarios/{id}/preferencias", original.getId()).header("Authorization", authorization))
                .andExpect(status().isOk());
        mvc.perform(get("/v1/usuarios/{id}/preferencias", novoTitular.getId()).header("Authorization", authorization))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"raioBuscaKm\":null}", "{\"raioBuscaKm\":0}",
            "{\"raioBuscaKm\":2}", "{\"raioBuscaKm\":4}", "{\"raioBuscaKm\":6}",
            "{\"raioBuscaKm\":11}", "{\"raioBuscaKm\":-1}", "{\"raioBuscaKm\":3.5}",
            "{\"raioBuscaKm\":\"3\"}", "{\"raioBuscaKm\":true}"})
    void shouldRejectInvalidPreferencesAndPreservePreviousValue(String payload) throws Exception {
        Usuario usuario = usuario("invalido@example.com");
        mvc.perform(patch("/v1/usuarios/{id}/preferencias", usuario.getId())
                        .header("Authorization", auth(usuario)).contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest());
        assertEquals(5, usuarios.findById(usuario.getId()).orElseThrow().getRaioBuscaKm());
    }

    @Test
    void shouldApplySavedRadiusWhenOmittedAndAllowExplicitOverrideWithoutPersistingIt() throws Exception {
        Usuario usuario = usuario("proximidade-pref@example.com");
        String authorization = auth(usuario);
        loja(usuario, "A", 0.005);
        loja(usuario, "B", 0.02);
        loja(usuario, "C", 0.04);
        loja(usuario, "D", 0.07);
        int expected = 0;
        for (int raio : new int[]{1, 3, 5, 10}) {
            mvc.perform(patch("/v1/usuarios/{id}/preferencias", usuario.getId())
                            .header("Authorization", authorization).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"raioBuscaKm\":" + raio + "}"))
                    .andExpect(status().isOk());
            mvc.perform(get("/v1/estabelecimentos/proximos").header("Authorization", authorization)
                            .param("latitude", "0").param("longitude", "0"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(++expected));
        }
        mvc.perform(patch("/v1/usuarios/{id}/preferencias", usuario.getId()).header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"raioBuscaKm\":1}"))
                .andExpect(status().isOk());
        mvc.perform(get("/v1/estabelecimentos/proximos").header("Authorization", authorization)
                        .param("latitude", "0").param("longitude", "0").param("raioKm", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(4));
        mvc.perform(get("/v1/usuarios/{id}/preferencias", usuario.getId()).header("Authorization", authorization))
                .andExpect(status().isOk()).andExpect(jsonPath("$.raioBuscaKm").value(1));
    }

    @Test
    void shouldAddAndRemoveFavoritesIdempotentlyAndReturnFullAddress() throws Exception {
        Usuario usuario = usuario("favoritos@example.com");
        String authorization = auth(usuario);
        var loja = loja(usuario, "Mercado favorito", 0.0);
        mvc.perform(get("/v1/usuarios/{id}/favoritos", usuario.getId()).header("Authorization", authorization))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/v1/usuarios/{id}/favoritos/{loja}", usuario.getId(), loja.getId())
                            .header("Authorization", authorization))
                    .andExpect(status().isNoContent());
        }
        entityManager.flush();
        entityManager.clear();
        assertEquals(1, favoritos.count());
        mvc.perform(get("/v1/usuarios/{id}/favoritos", usuario.getId()).header("Authorization", authorization))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(loja.getId()))
                .andExpect(jsonPath("$[0].foto").value("https://example.com/loja.png"))
                .andExpect(jsonPath("$[0].endereco.logradouro").value("Rua A"))
                .andExpect(jsonPath("$[0].endereco.numero").value("10"));
        for (int i = 0; i < 2; i++) {
            mvc.perform(delete("/v1/usuarios/{id}/favoritos/{loja}", usuario.getId(), loja.getId())
                            .header("Authorization", authorization))
                    .andExpect(status().isNoContent());
        }
        assertEquals(0, favoritos.count());
        assertTrue(estabelecimentos.findById(loja.getId()).orElseThrow().isAtivo());
    }

    @Test
    void shouldKeepFavoritesSeparateForEachUserAndHideInactiveEstablishments() throws Exception {
        Usuario usuario = usuario("fav-a@example.com");
        Usuario outro = usuario("fav-b@example.com");
        var loja = loja(usuario, "Mercado", 0.0);
        for (Usuario owner : new Usuario[]{usuario, outro}) {
            mvc.perform(put("/v1/usuarios/{id}/favoritos/{loja}", owner.getId(), loja.getId())
                            .header("Authorization", auth(owner)))
                    .andExpect(status().isNoContent());
        }
        assertEquals(2, favoritos.count());
        mvc.perform(delete("/v1/usuarios/{id}/favoritos/{loja}", outro.getId(), loja.getId())
                        .header("Authorization", auth(outro)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/v1/usuarios/{id}/favoritos", usuario.getId()).header("Authorization", auth(usuario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/v1/usuarios/{id}/favoritos", outro.getId()).header("Authorization", auth(outro)))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        loja.setAtivo(false);
        estabelecimentos.saveAndFlush(loja);
        mvc.perform(get("/v1/usuarios/{id}/favoritos", usuario.getId()).header("Authorization", auth(usuario)))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(put("/v1/usuarios/{id}/favoritos/{loja}", usuario.getId(), loja.getId())
                        .header("Authorization", auth(usuario)))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/v1/usuarios/{id}/favoritos/{loja}", usuario.getId(), loja.getId())
                        .header("Authorization", auth(usuario)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldEnforceOwnershipAndAuthenticationAndAllowAdmin() throws Exception {
        Usuario usuario = usuario("owner@example.com");
        Usuario outro = usuario("stranger@example.com");
        var loja = loja(usuario, "Loja", 0.0);
        String base = "/v1/usuarios/" + usuario.getId();
        String stranger = auth(outro);
        mvc.perform(get(base + "/preferencias").header("Authorization", stranger)).andExpect(status().isForbidden());
        mvc.perform(patch(base + "/preferencias").header("Authorization", stranger)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"raioBuscaKm\":3}"))
                .andExpect(status().isForbidden());
        mvc.perform(get(base + "/favoritos").header("Authorization", stranger)).andExpect(status().isForbidden());
        mvc.perform(put(base + "/favoritos/" + loja.getId()).header("Authorization", stranger)).andExpect(status().isForbidden());
        mvc.perform(delete(base + "/favoritos/" + loja.getId()).header("Authorization", stranger)).andExpect(status().isForbidden());
        mvc.perform(get(base + "/preferencias")).andExpect(status().isUnauthorized());
        mvc.perform(get(base + "/favoritos")).andExpect(status().isUnauthorized());
        mvc.perform(put(base + "/favoritos/" + loja.getId())).andExpect(status().isUnauthorized());
        mvc.perform(put(base + "/favoritos/999999").header("Authorization", auth(usuario)))
                .andExpect(status().isNotFound());

        outro.setRole(UsuarioRole.ADMIN);
        usuarios.saveAndFlush(outro);
        String admin = auth(outro);
        mvc.perform(patch(base + "/preferencias").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"raioBuscaKm\":10}"))
                .andExpect(status().isOk());
        mvc.perform(put(base + "/favoritos/" + loja.getId()).header("Authorization", admin))
                .andExpect(status().isNoContent());
        mvc.perform(get(base + "/favoritos").header("Authorization", admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void favoritosDeProdutosSaoPrivadosIdempotentesEFiltramInativos() throws Exception {
        var dono = usuario("produto-dono@example.com");
        var outro = usuario("produto-outro@example.com");
        var produto = produtos.saveAndFlush(Produto.builder().nome("Leite").descricao("Integral")
                .marca("Exemplo").categoria(ProdutoEnum.ALIMENTO).codigoDeBarras("7891000100103")
                .usuario(outro).build());

        String lista = "/v1/usuarios/" + dono.getId() + "/produtos-favoritos";
        String favorito = lista + "/" + produto.getId();
        String token = auth(dono);
        String outroToken = auth(outro);

        mvc.perform(get(lista)).andExpect(status().isUnauthorized());
        mvc.perform(get(lista).header("Authorization", outroToken)).andExpect(status().isForbidden());
        mvc.perform(put(favorito).header("Authorization", outroToken)).andExpect(status().isForbidden());
        mvc.perform(delete(favorito).header("Authorization", outroToken)).andExpect(status().isForbidden());

        for (int i = 0; i < 2; i++) {
            mvc.perform(put(favorito).header("Authorization", token)).andExpect(status().isNoContent());
        }
        mvc.perform(get(lista).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(produto.getId()))
                .andExpect(jsonPath("$[0].codigoDeBarras").value("7891000100103"));
        mvc.perform(get("/v1/usuarios/{id}/produtos-favoritos", outro.getId()).header("Authorization", outroToken))
                .andExpect(status().isOk()).andExpect(content().json("[]"));

        produto.setAtivo(false);
        produtos.saveAndFlush(produto);
        mvc.perform(get(lista).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(put(favorito).header("Authorization", token)).andExpect(status().isNotFound());

        for (int i = 0; i < 2; i++) {
            mvc.perform(delete(favorito).header("Authorization", token)).andExpect(status().isNoContent());
        }
        produto.setAtivo(true);
        produtos.saveAndFlush(produto);
        mvc.perform(get(lista).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }
}
