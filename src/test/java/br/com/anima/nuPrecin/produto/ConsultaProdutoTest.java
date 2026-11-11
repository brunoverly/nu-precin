package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.dto.ConsultaProdutoResponseDto.Sugestao;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ConsultaProdutoTest {
    @Autowired private MockMvc mvc;
    @Autowired private ProdutoRepository produtos;
    @Autowired private UsuarioRepository usuarios;
    @MockitoBean private OpenFoodFactsClient openFoodFacts;

    private Usuario usuario() {
        return usuarios.saveAndFlush(Usuario.builder().nome("Maria")
                .email("catalogo@example.com").senha("hash").build());
    }

    @Test
    void encontraGtinEquivalenteEmDigitalLinkEEmQrCadastradoSemConsultarProvedor() throws Exception {
        var usuario = usuario();
        var produto = produtos.saveAndFlush(Produto.builder().nome("Produto local")
                .descricao("Descrição").marca("Marca").categoria(ProdutoEnum.BEBIDA)
                .codigoDeBarras("7894900011517").qrCode("QR-PROPRIO").usuario(usuario).build());

        mvc.perform(get("/v1/produtos/por-codigo/{codigo}", "07894900011517").with(user(usuario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.origem").value("LOCAL"))
                .andExpect(jsonPath("$.produto.codigoDeBarras").value("7894900011517"));

        for (String qr : new String[]{"https://exemplo.com/01/07894900011517/21/123", "QR-PROPRIO"}) {
            mvc.perform(get("/v1/produtos/por-qr-code").param("conteudo", qr).with(user(usuario)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.produto.id").value(produto.getId()));
        }
        verifyNoInteractions(openFoodFacts);
    }

    @Test
    void devolveSugestaoSemCadastrarERejeitaQrIncompativel() throws Exception {
        var usuario = usuario();
        when(openFoodFacts.buscar("7891000100103")).thenReturn(Optional.of(
                new Sugestao("7891000100103", "Leite condensado", null, "Moça",
                        ProdutoEnum.ALIMENTO, null, "395 g", "https://world.openfoodfacts.org/product/7891000100103", null, null)));

        mvc.perform(get("/v1/produtos/por-qr-code").param("conteudo", "7891000100103").with(user(usuario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.origem").value("OPEN_FOOD_FACTS"))
                .andExpect(jsonPath("$.sugestao.marca").value("Moça"));
        org.junit.jupiter.api.Assertions.assertEquals(0, produtos.count());

        mvc.perform(get("/v1/produtos/por-qr-code")
                        .param("conteudo", "https://exemplo.com/nota-fiscal").with(user(usuario)))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/v1/produtos/por-codigo/{codigo}", "7894900011517").with(user(usuario)))
                .andExpect(status().isNotFound());
    }
    @Test
    void cadastraMedidaEPreservaNoPutQuandoOmitida() throws Exception {
        var usuario = usuario();
        String dados = """
                {"nome":"Leite","descricao":"Integral","marca":"Exemplo",
                "codigoDeBarras":"7891000100103","categoria":"LATICINIOS_E_OVOS","idUsuario":%d}
                """.formatted(usuario.getId()).stripTrailing();
        String comMedida = dados.substring(0, dados.length() - 1)
                + ",\"quantidadeEmbalagem\":395,\"unidadeEmbalagem\":\"G\"}";
        mvc.perform(post("/v1/produtos").with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(comMedida))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidadeEmbalagem").value(395))
                .andExpect(jsonPath("$.unidadeEmbalagem").value("G"));

        mvc.perform(get("/v1/produtos").param("categoria", "LATICINIOS_E_OVOS").with(user(usuario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].categoria").value("LATICINIOS_E_OVOS"));
        mvc.perform(get("/v1/produtos").param("categoria", "ALIMENTO").with(user(usuario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));

        var produto = produtos.findByCodigoDeBarrasInAndAtivoTrue(java.util.List.of("7891000100103")).orElseThrow();
        mvc.perform(put("/v1/produtos/{id}", produto.getId()).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(dados))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEmbalagem").value(395));

        String incompleto = dados.substring(0, dados.length() - 1) + ",\"quantidadeEmbalagem\":395}";
        mvc.perform(put("/v1/produtos/{id}", produto.getId()).with(user(usuario))
                        .contentType(MediaType.APPLICATION_JSON).content(incompleto))
                .andExpect(status().isBadRequest());
    }
}
