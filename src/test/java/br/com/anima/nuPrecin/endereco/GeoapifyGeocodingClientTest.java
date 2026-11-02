package br.com.anima.nuPrecin.endereco;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class GeoapifyGeocodingClientTest {
    private MockRestServiceServer server;
    private GeoapifyGeocodingClient client;
    private final Endereco endereco = Endereco.builder().logradouro("Rua São João & Silva")
            .numero("4500").bairro("Centro").cidade("Belo Horizonte").estado("MG").cep("31270010").build();

    @BeforeEach
    void setup() {
        var builder = RestClient.builder().baseUrl("https://api.geoapify.com");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new GeoapifyGeocodingClient(builder.build(), "test-key");
    }

    @Test
    void shouldEncodeAddressAndReadCoordinatesFromPreciseBrazilianResult() {
        server.expect(request -> {
            String query = URLDecoder.decode(request.getURI().getRawQuery(), StandardCharsets.UTF_8);
            assertTrue(query.contains("Rua São João & Silva, 4500, Centro, Belo Horizonte, MG, 31270010, Brasil"));
            assertTrue(query.contains("filter=countrycode:br"));
            assertTrue(query.contains("format=json"));
            assertTrue(query.contains("limit=1"));
        }).andRespond(withSuccess("""
                {"results":[{"lat":-19.86,"lon":-43.95,"country_code":"br",
                 "result_type":"building","rank":{"confidence":0.95}}]}
                """, MediaType.APPLICATION_JSON));

        var result = client.geocode(endereco).orElseThrow();
        assertEquals(-19.86, result.latitude());
        assertEquals(-43.95, result.longitude());
        server.verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"results\":[]}",
            "{\"results\":[{\"lat\":-19.86,\"lon\":-43.95,\"country_code\":\"br\",\"result_type\":\"city\",\"rank\":{\"confidence\":1}}]}",
            "{\"results\":[{\"lat\":-19.86,\"lon\":-43.95,\"country_code\":\"br\",\"result_type\":\"street\",\"rank\":{\"confidence\":0.1}}]}",
            "{\"results\":[{\"lat\":91,\"lon\":-43.95,\"country_code\":\"br\",\"result_type\":\"building\",\"rank\":{\"confidence\":1}}]}",
            "{\"results\":[{\"lat\":-19.86,\"country_code\":\"br\",\"result_type\":\"building\",\"rank\":{\"confidence\":1}}]}",
            "{\"results\":[{\"lat\":-19.86,\"lon\":-43.95,\"country_code\":\"us\",\"result_type\":\"building\",\"rank\":{\"confidence\":1}}]}"
    })
    void shouldLeaveCoordinatesEmptyForMissingOrUnreliableMatches(String body) {
        server.expect(anything()).andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
        assertTrue(client.geocode(endereco).isEmpty());
        server.verify();
    }

    @Test
    void shouldAllowAnotherAttemptAfterRateLimitResponse() {
        server.expect(anything()).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        server.expect(anything()).andRespond(withSuccess("""
                {"results":[{"lat":-19.86,"lon":-43.95,"country_code":"br",
                 "result_type":"amenity","rank":{"confidence":1}}]}
                """, MediaType.APPLICATION_JSON));
        assertTrue(client.geocode(endereco).isEmpty());
        assertTrue(client.geocode(endereco).isPresent());
        server.verify();
    }

    @Test
    void shouldNotFailRegistrationWhenRequestTimesOut() {
        server.expect(anything()).andRespond(withException(new SocketTimeoutException()));
        assertTrue(client.geocode(endereco).isEmpty());
        server.verify();
    }

    @Test
    void shouldSkipHttpWhenKeyIsMissing() {
        var withoutKey = new GeoapifyGeocodingClient(RestClient.create(), "");
        assertFalse(withoutKey.isConfigured());
        assertTrue(withoutKey.geocode(endereco).isEmpty());
    }
}
