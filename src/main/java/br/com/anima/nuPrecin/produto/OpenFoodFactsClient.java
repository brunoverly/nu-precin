package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.exception.ServicoExternoException;
import br.com.anima.nuPrecin.produto.dto.ConsultaProdutoResponseDto.Sugestao;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@Component
public class OpenFoodFactsClient {
    private static final String BASE_URL = "https://world.openfoodfacts.org";
    private static final String FIELDS = "code,product_name_pt,product_name,generic_name_pt,generic_name,"
            + "brands,categories_tags,image_front_url,quantity,product_quantity,product_quantity_unit";
    private final RestClient client;
    private final Deque<Long> chamadas = new ArrayDeque<>();

    public OpenFoodFactsClient(RestClient.Builder builder,
                               @Value("${app.catalogo.open-food-facts-user-agent}") String userAgent) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        client = builder.baseUrl(BASE_URL).requestFactory(factory)
                .defaultHeader("User-Agent", userAgent).build();
    }

    public Optional<Sugestao> buscar(String codigo) {
        reservarConsulta();
        var variantes = CodigoProduto.variantes(codigo);
        String codigoConsulta = variantes.get(variantes.size() - 1);
        try {
            var resposta = client.get().uri(builder -> builder.path("/api/v3/product/{codigo}")
                            .queryParam("fields", FIELDS).build(codigoConsulta))
                    .retrieve().body(JsonNode.class);
            if (resposta == null) {
                throw new ServicoExternoException("Resposta vazia do Open Food Facts.", null);
            }
            JsonNode produto = resposta.path("product");
            if (!produto.isObject()) {
                if ("product_not_found".equals(resposta.path("result").path("id").asText())) {
                    return Optional.empty();
                }
                throw new ServicoExternoException("Resposta inválida do Open Food Facts.", null);
            }
            if (!variantes.contains(produto.path("code").asText())) {
                throw new ServicoExternoException("Código retornado pelo Open Food Facts diverge da consulta.", null);
            }

            UnidadeEmbalagem unidade = switch (produto.path("product_quantity_unit").asText("")) {
                case "g" -> UnidadeEmbalagem.G;
                case "ml" -> UnidadeEmbalagem.ML;
                default -> null;
            };
            BigDecimal quantidade = unidade == null ? null : quantidadeNormalizada(produto);

            return Optional.of(new Sugestao(codigo,
                    texto(produto, "product_name_pt", "product_name", 250),
                    texto(produto, "generic_name_pt", "generic_name", 500),
                    texto(produto, "brands", "brands", 250),
                    categoria(produto),
                    texto(produto, "image_front_url", "image_front_url", 500),
                    texto(produto, "quantity", "quantity", 250),
                    BASE_URL + "/product/" + produto.path("code").asText(),
                    quantidade, quantidade == null ? null : unidade));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ServicoExternoException("Consulta ao Open Food Facts indisponível. Tente novamente ou use o cadastro manual.", ex);
        }
    }

    private BigDecimal quantidadeNormalizada(JsonNode produto) {
        String valor = produto.path("product_quantity").asText("");
        if (!valor.matches("[0-9]{1,12}(\\.[0-9]{1,12})?")) return null;
        BigDecimal quantidade = new BigDecimal(valor).stripTrailingZeros();
        return quantidade.signum() > 0 && quantidade.scale() <= 3 ? quantidade : null;
    }

    private synchronized void reservarConsulta() {
        long agora = System.nanoTime();
        while (!chamadas.isEmpty() && agora - chamadas.peekFirst() >= Duration.ofMinutes(1).toNanos()) {
            chamadas.removeFirst();
        }
        if (chamadas.size() >= 15) {
            throw new ServicoExternoException("Limite de consultas ao Open Food Facts atingido. Tente novamente em instantes.", null);
        }
        chamadas.addLast(agora);
    }

    private String texto(JsonNode produto, String preferido, String alternativo, int limite) {
        String valor = produto.path(preferido).asText("").strip();
        if (valor.isEmpty()) valor = produto.path(alternativo).asText("").strip();
        return valor.isEmpty() || valor.length() > limite ? null : valor;
    }

    private ProdutoEnum categoria(JsonNode produto) {
        Set<String> tags = new HashSet<>();
        for (JsonNode tag : produto.path("categories_tags")) {
            tags.add(tag.asText().toLowerCase(Locale.ROOT));
        }

        if (tem(tags, "en:beers", "en:non-alcoholic-beers", "pt:cervejas")) return ProdutoEnum.CERVEJAS;
        if (tem(tags, "en:wines", "en:spirits", "pt:vinhos", "pt:destilados")) return ProdutoEnum.VINHOS_E_DESTILADOS;
        if (tem(tags, "en:fruit-juices", "en:fruit-nectars", "en:fruit-and-vegetable-juices", "pt:sucos")) return ProdutoEnum.SUCOS;
        if (tem(tags, "en:soft-drinks", "en:sodas", "en:colas", "pt:refrigerantes")) return ProdutoEnum.REFRIGERANTES;
        if (tem(tags, "en:waters", "en:mineral-waters", "pt:aguas")) return ProdutoEnum.AGUAS;
        if (tem(tags, "en:coffees", "en:teas", "pt:cafes", "pt:chas")) return ProdutoEnum.CAFES_E_CHAS;
        if (tem(tags, "en:frozen-foods", "pt:congelados")) return ProdutoEnum.CONGELADOS;
        if (tem(tags, "en:charcuteries", "en:sausages", "en:hams", "pt:embutidos")) return ProdutoEnum.FRIOS_E_EMBUTIDOS;
        if (tem(tags, "en:fish", "en:seafood", "pt:peixes")) return ProdutoEnum.PEIXES_E_FRUTOS_DO_MAR;
        if (tem(tags, "en:meats", "en:poultries", "pt:carnes")) return ProdutoEnum.CARNES;
        if (tem(tags, "en:dairies", "en:eggs", "pt:laticinios", "pt:ovos")) return ProdutoEnum.LATICINIOS_E_OVOS;
        if (tem(tags, "en:breads", "en:viennoiseries", "pt:paes")) return ProdutoEnum.PADARIA;
        if (tem(tags, "en:snacks", "en:confectioneries", "pt:doces")) return ProdutoEnum.DOCES_E_SNACKS;
        if (tem(tags, "en:pastas", "en:rice", "en:flours", "en:canned-foods", "en:legumes")) return ProdutoEnum.MERCEARIA;
        if (tem(tags, "en:fresh-fruits", "en:fresh-vegetables", "en:fruits", "en:vegetables",
                "pt:frutas", "pt:verduras", "pt:legumes-frescos")) return ProdutoEnum.HORTIFRUTI;
        if (tem(tags, "en:beverages", "pt:bebida", "pt:bebidas")) return ProdutoEnum.BEBIDA;
        return tags.isEmpty() ? null : ProdutoEnum.ALIMENTO;
    }

    private boolean tem(Set<String> tags, String... categorias) {
        for (String categoria : categorias) {
            if (tags.contains(categoria)) return true;
        }
        return false;
    }
}
