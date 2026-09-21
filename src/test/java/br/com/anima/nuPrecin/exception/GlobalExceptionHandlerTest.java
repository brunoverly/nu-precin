package br.com.anima.nuPrecin.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnPayloadTooLargeForOversizedUpload() {
        ResponseEntity<ErrorResponse> response = handler.handleMaxUploadSize(
                new MaxUploadSizeExceededException(5 * 1024 * 1024L),
                request("/v1/produtos/1/imagem")
        );

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals("PAYLOAD_TOO_LARGE", response.getBody().error());
    }

    @Test
    void shouldReturnBadRequestWhenMultipartFileIsMissing() {
        ResponseEntity<ErrorResponse> response = handler.handleMissingMultipartPart(
                new MissingServletRequestPartException("file"),
                request("/v1/usuarios/1/foto")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", response.getBody().error());
    }

    @Test
    void shouldReturnBadGatewayForExternalServiceFailure() {
        ResponseEntity<ErrorResponse> response = handler.handleExternalService(
                new ServicoExternoException("Serviço externo indisponível.", new RuntimeException()),
                request("/v1/auth/registro")
        );

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("BAD_GATEWAY", response.getBody().error());
    }

    private HttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }
}
