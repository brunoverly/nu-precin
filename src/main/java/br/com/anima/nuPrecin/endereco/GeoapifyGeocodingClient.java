package br.com.anima.nuPrecin.endereco;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GeoapifyGeocodingClient {
    private static final Logger log = LoggerFactory.getLogger(GeoapifyGeocodingClient.class);
    private static final Set<String> ADDRESS_TYPES = Set.of("building", "amenity", "street");
    private final RestClient client;
    private final String apiKey;
    private long nextRequestNanos;

    public GeoapifyGeocodingClient(@Qualifier("geoapifyRestClient") RestClient client,
                                  @Value("${key.geoapify-key:}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    // Serializa as chamadas e limita a menos de cinco consultas por segundo nesta instância.
    public synchronized Optional<Coordinates> geocode(Endereco endereco) {
        if (!isConfigured()) return Optional.empty();
        String address = Stream.of(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(),
                        endereco.getCidade(), endereco.getEstado(), endereco.getCep(), "Brasil")
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(", "));
        try {
            long waitNanos = nextRequestNanos - System.nanoTime();
            if (waitNanos > 0) Thread.sleep(java.time.Duration.ofNanos(waitNanos).toMillis() + 1);
            nextRequestNanos = System.nanoTime() + 250_000_000L;
            JsonNode response = client.get().uri(uri -> uri.path("/v1/geocode/search")
                            .queryParam("text", "{address}")
                            .queryParam("filter", "countrycode:br")
                            .queryParam("lang", "pt").queryParam("format", "json")
                            .queryParam("limit", 1).queryParam("apiKey", "{key}")
                            .build(address, apiKey))
                    .retrieve().body(JsonNode.class);
            if (response == null) return Optional.empty();
            JsonNode result = response.path("results").path(0);
            if (!ADDRESS_TYPES.contains(result.path("result_type").asText())
                    || !"br".equals(result.path("country_code").asText())
                    || result.path("rank").path("confidence").asDouble() < 0.7
                    || !result.path("lat").isNumber() || !result.path("lon").isNumber()) {
                return Optional.empty();
            }
            double lat = result.path("lat").asDouble();
            double lon = result.path("lon").asDouble();
            if (!Double.isFinite(lat) || !Double.isFinite(lon)
                    || lat < -90 || lat > 90 || lon < -180 || lon > 180) return Optional.empty();
            return Optional.of(new Coordinates(lat, lon));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (RestClientResponseException ex) {
            // A mensagem/stack trace do cliente pode conter a URL com a chave; nunca registrá-los.
            log.warn("Geoapify indisponível (HTTP {}). Coordenadas permanecem vazias.", ex.getStatusCode().value());
        } catch (RestClientException ex) {
            log.warn("Falha na comunicação com Geoapify. Coordenadas permanecem vazias.");
        }
        return Optional.empty();
    }

    public record Coordinates(double latitude, double longitude) {}
}
