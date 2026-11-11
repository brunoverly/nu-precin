package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.exception.ServicoExternoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class OpenFoodFactsClientTest {
    @Test
    void mapeiaCamposParciaisELimitaConsultasExternas() {
        var builder = RestClient.builder().requestInterceptor((request, body, execution) -> {
            assertEquals("/api/v3/product/7894900011517", request.getURI().getPath());
            assertEquals("NuPrecin/teste", request.getHeaders().getFirst("User-Agent"));
            var response = new MockClientHttpResponse("""
                    {"product":{"code":"7894900011517","product_name_pt":"Refrigerante",
                    "brands":"Coca-Cola","categories_tags":["pt:Bebida","en:colas"],"quantity":"2 l",
                    "product_quantity":2000,"product_quantity_unit":"ml"}}
                    """.getBytes(java.nio.charset.StandardCharsets.UTF_8), HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
        var client = new OpenFoodFactsClient(builder, "NuPrecin/teste");
        for (int i = 0; i < 15; i++) {
            var sugestao = client.buscar("07894900011517").orElseThrow();
            assertEquals("Refrigerante", sugestao.nome());
            assertEquals(ProdutoEnum.REFRIGERANTES, sugestao.categoria());
            assertEquals("2 l", sugestao.quantidade());
            assertEquals(2000, sugestao.quantidadeEmbalagem().intValueExact());
            assertEquals(UnidadeEmbalagem.ML, sugestao.unidadeEmbalagem());
            assertNull(sugestao.descricao());
        }
        assertThrows(ServicoExternoException.class, () -> client.buscar("07894900011517"));
    }
}
