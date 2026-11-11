package br.com.anima.nuPrecin.preco;

import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoTipo;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoEnum;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.anima.nuPrecin.promocao.Promocao;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PrecoObservadoTest {
    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private ProdutoRepository produtos;
    @Autowired private EstabelecimentoRepository lojas;
    @Autowired private EntityManager entityManager;

    @Test
    void preservaObservacoesEConsultaPorDataELoja() throws Exception {
        var usuario = usuarios.saveAndFlush(Usuario.builder().nome("Maria").email("precos@example.com").senha("hash").build());
        var produto = produtos.saveAndFlush(Produto.builder().nome("Leite").descricao("Integral")
                .marca("Exemplo").categoria(ProdutoEnum.ALIMENTO).codigoDeBarras("7891000100103").usuario(usuario).build());
        var loja = loja(usuario, "Loja A");
        var outra = loja(usuario, "Loja B");
        String recente = dados(produto, loja, "8.49", Instant.parse("2025-01-03T12:00:00Z"));
        String anterior = dados(produto, loja, "9.50", Instant.parse("2025-01-01T12:00:00Z"));

        mvc.perform(post("/v1/precos").contentType(MediaType.APPLICATION_JSON).content(recente))
                .andExpect(status().isUnauthorized());
        registrar(usuario, recente).andExpect(jsonPath("$.idUsuario").value(usuario.getId()))
                .andExpect(jsonPath("$.origem").value("MANUAL"));
        registrar(usuario, anterior);
        registrar(usuario, dados(produto, outra, "8.99", Instant.parse("2025-01-02T12:00:00Z")));

        mvc.perform(post("/v1/precos").with(user(usuario)).contentType(MediaType.APPLICATION_JSON)
                        .content(dados(produto, loja, "1.00", Instant.now().plusSeconds(3600))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/v1/precos").with(user(usuario)).contentType(MediaType.APPLICATION_JSON)
                        .content(dados(produto, loja, "8.499", Instant.parse("2025-01-01T12:00:00Z"))))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();

        mvc.perform(get("/v1/produtos/{id}/historico-precos", produto.getId()).with(user(usuario))
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].valor").value(8.49))
                .andExpect(jsonPath("$.content[1].idEstabelecimento").value(outra.getId()));
        mvc.perform(get("/v1/produtos/{id}/historico-precos", produto.getId()).with(user(usuario))
                        .param("idEstabelecimento", loja.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].valor").value(8.49))
                .andExpect(jsonPath("$.content[1].valor").value(9.50));
    }

    @Test
    void promocaoRegistraVersoesComVigenciaEPreservaPrecoAnterior() throws Exception {
        var usuario = usuarios.saveAndFlush(Usuario.builder().nome("Maria").email("oferta@example.com").senha("hash").build());
        var produto = produtos.saveAndFlush(Produto.builder().nome("Leite").descricao("Integral")
                .marca("Exemplo").categoria(ProdutoEnum.ALIMENTO).codigoDeBarras("7891000100103").usuario(usuario).build());
        var loja = loja(usuario, "Loja da oferta");
        var inicio = LocalDateTime.now().plusDays(1).withNano(0);
        String oferta = """
                {"codigoBarras":"7891000100103","idEstabelecimento":%d,"idUsuario":%d,
                "precoOriginal":10.00,"precoPromocao":8.49,"dataInicio":"%s","dataFim":"%s"}
                """.formatted(loja.getId(), usuario.getId(), inicio, inicio.plusDays(1));

        String resposta = mvc.perform(post("/v1/promocoes").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(oferta))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long idPromocao = new com.fasterxml.jackson.databind.ObjectMapper().readTree(resposta).path("id").asLong();
        String revisao = oferta.replace("8.49", "7.99");
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/v1/promocoes/{id}", idPromocao).with(user(usuario))
                            .contentType(MediaType.APPLICATION_JSON).content(revisao))
                    .andExpect(status().isOk());
        }

        entityManager.flush();
        entityManager.clear();

        mvc.perform(get("/v1/produtos/{id}/historico-precos", produto.getId()).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].valor").value(7.99))
                .andExpect(jsonPath("$.content[0].origem").value("PROMOCAO"))
                .andExpect(jsonPath("$.content[0].idPromocao").value(idPromocao))
                .andExpect(jsonPath("$.content[0].precoOriginal").value(10.00))
                .andExpect(jsonPath("$.content[0].inicioVigencia").exists())
                .andExpect(jsonPath("$.content[0].fimVigencia").exists())
                .andExpect(jsonPath("$.content[1].valor").value(8.49));
    }

    @Test
    void comparaMenorOfertaVigentePorLoja() throws Exception {
        var usuario = usuarios.saveAndFlush(Usuario.builder().nome("Maria").email("comparativo@example.com").senha("hash").build());
        var produto = produtos.saveAndFlush(Produto.builder().nome("Leite").descricao("Integral")
                .marca("Exemplo").categoria(ProdutoEnum.ALIMENTO).codigoDeBarras("7891000100103").usuario(usuario).build());
        var lojaA = loja(usuario, "Loja A");
        var lojaB = loja(usuario, "Loja B");
        var agora = Promocao.agoraNaVigencia();
        String endpoint = "/v1/produtos/{id}/comparativo-precos";
        mvc.perform(get(endpoint, produto.getId()).with(user(usuario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        oferta(produto, lojaA, usuario, "8.99", agora.minusDays(1), agora.plusDays(1));
        var melhor = oferta(produto, lojaA, usuario, "7.99", agora.minusDays(1), agora.plusDays(1));
        oferta(produto, lojaA, usuario, "7.99", agora.minusDays(1), agora.plusDays(1));
        oferta(produto, lojaB, usuario, "6.99", agora.minusDays(1), agora.plusDays(1));
        oferta(produto, lojaB, usuario, "1.99", agora.plusDays(1), agora.plusDays(2));
        oferta(produto, lojaB, usuario, "1.99", agora.minusDays(2), agora.minusDays(1));
        var inativa = oferta(produto, lojaB, usuario, "1.99", agora.minusDays(1), agora.plusDays(1));
        inativa.setAtivo(false);
        var lojaInativa = loja(usuario, "Loja inativa");
        lojaInativa.setAtivo(false);
        oferta(produto, lojaInativa, usuario, "1.99", agora.minusDays(1), agora.plusDays(1));

        entityManager.flush();
        entityManager.clear();

        mvc.perform(get(endpoint, produto.getId()).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idEstabelecimento").value(lojaB.getId()))
                .andExpect(jsonPath("$[0].precoPromocao").value(6.99))
                .andExpect(jsonPath("$[1].nomeEstabelecimento").value("Loja A"))
                .andExpect(jsonPath("$[1].idPromocao").value(melhor.getId()))
                .andExpect(jsonPath("$[1].precoPromocao").value(7.99));
        mvc.perform(get(endpoint, Long.MAX_VALUE).with(user(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizaPrecoDeOfertaJaVigenteEGravaHistorico() throws Exception {
        var usuario = usuarios.saveAndFlush(Usuario.builder().nome("Maria").email("vigente@example.com").senha("hash").build());
        var produto = produtos.saveAndFlush(Produto.builder().nome("Leite").descricao("Integral")
                .marca("Exemplo").categoria(ProdutoEnum.ALIMENTO).codigoDeBarras("7891000100103").usuario(usuario).build());
        var loja = loja(usuario, "Loja");
        var inicio = Promocao.agoraNaVigencia().minusDays(1).withNano(0);
        var fim = inicio.plusDays(2);
        var promocao = oferta(produto, loja, usuario, "8.49", inicio, fim);
        String revisao = """
                {"codigoBarras":"7891000100103","idEstabelecimento":%d,"idUsuario":%d,
                "precoOriginal":10.00,"precoPromocao":7.99,"dataInicio":"%s","dataFim":"%s"}
                """.formatted(loja.getId(), usuario.getId(), inicio, fim);

        mvc.perform(put("/v1/promocoes/{id}", promocao.getId()).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(revisao))
                .andExpect(status().isOk());
        mvc.perform(get("/v1/produtos/{id}/historico-precos", produto.getId()).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].valor").value(7.99));
        mvc.perform(get("/v1/produtos/{id}/comparativo-precos", produto.getId()).with(user(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].precoPromocao").value(7.99));
    }

    @Test
    void ofertaDeEstoqueSemFimExpiraEVoltaAoComparativoAposConfirmacao() throws Exception {
        var usuario = usuarios.saveAndFlush(Usuario.builder().nome("Maria").email("estoque@example.com").senha("hash").build());
        var produto = produtos.saveAndFlush(Produto.builder().nome("Leite").descricao("Integral")
                .marca("Exemplo").categoria(ProdutoEnum.ALIMENTO).codigoDeBarras("7891000100103").usuario(usuario).build());
        var loja = loja(usuario, "Loja");
        var inicio = Promocao.agoraNaVigencia().minusHours(1).withNano(0);
        String dados = """
                {"codigoBarras":"7891000100103","idEstabelecimento":%d,"idUsuario":%d,
                "precoOriginal":10.00,"precoPromocao":7.99,"dataInicio":"%s","enquantoDurarEstoque":true}
                """.formatted(loja.getId(), usuario.getId(), inicio);
        String resposta = mvc.perform(post("/v1/promocoes").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(dados))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enquantoDurarEstoque").value(true))
                .andExpect(jsonPath("$.ultimaConfirmacaoEm").exists())
                .andReturn().getResponse().getContentAsString();
        long id = new com.fasterxml.jackson.databind.ObjectMapper().readTree(resposta).path("id").asLong();

        String comparativo = "/v1/produtos/{id}/comparativo-precos";
        mvc.perform(get(comparativo, produto.getId()).with(user(usuario)))
                .andExpect(jsonPath("$[0].idPromocao").value(id));
        entityManager.find(Promocao.class, id).setUltimaConfirmacaoEm(Promocao.agoraNaVigencia().minusHours(25));
        entityManager.flush();

        mvc.perform(get(comparativo, produto.getId()).with(user(usuario)))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/v1/promocoes/{id}/confirmar-disponibilidade", id).with(user(usuario)))
                .andExpect(status().isOk());
        mvc.perform(get(comparativo, produto.getId()).with(user(usuario)))
                .andExpect(jsonPath("$[0].idPromocao").value(id));
        mvc.perform(get("/v1/produtos/{id}/historico-precos", produto.getId()).with(user(usuario)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].enquantoDurarEstoque").value(true))
                .andExpect(jsonPath("$.content[0].fimVigencia").doesNotExist());
        mvc.perform(post("/v1/promocoes").with(user(usuario)).contentType(MediaType.APPLICATION_JSON)
                        .content(dados.replace("true", "false")))
                .andExpect(status().isBadRequest());
    }

    private Promocao oferta(Produto produto, Estabelecimento loja, Usuario usuario, String valor,
                            LocalDateTime inicio, LocalDateTime fim) {
        var promocao = Promocao.builder().idProduto(produto.getId()).idEstabelecimento(loja.getId())
                .idUsuario(usuario.getId()).precoOriginal(new BigDecimal("10.00"))
                .precoPromocao(new BigDecimal(valor)).dataInicio(inicio).dataFim(fim).build();
        entityManager.persist(promocao);
        return promocao;
    }

    private org.springframework.test.web.servlet.ResultActions registrar(Usuario usuario, String dados) throws Exception {
        return mvc.perform(post("/v1/precos").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(dados))
                .andExpect(status().isCreated());
    }

    private String dados(Produto produto, Estabelecimento loja, String valor, Instant observadoEm) {
        return """
                {"idProduto":%d,"idEstabelecimento":%d,"valor":%s,"observadoEm":"%s"}
                """.formatted(produto.getId(), loja.getId(), valor, observadoEm);
    }

    private Estabelecimento loja(Usuario usuario, String nome) {
        return lojas.saveAndFlush(Estabelecimento.builder().nome(nome).usuario(usuario)
                .tipo(EstabelecimentoTipo.SUPERMERCADO).telefone("3133334455")
                .endereco(Endereco.builder().logradouro("Rua A").bairro("Centro")
                        .cidade("Belo Horizonte").estado("MG").build()).build());
    }
}
