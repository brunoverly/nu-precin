package br.com.anima.nuPrecin.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}")
    private String from;

    private final RestClient restClient = RestClient.create("https://api.resend.com");

    public void enviarCodigoConfirmacao(
            String email,
            String nome,
            String codigo,
            int validadeMinutos) {

        enviarCodigo(
                email,
                nome,
                codigo,
                validadeMinutos,
                "Verificação de conta",
                "Cole o código abaixo no seu aplicativo para confirmar sua identidade e acessar sua conta.",
                "Código de confirmação - NuPrecin"
        );
    }

    public void enviarCodigoResetSenha(
            String email,
            String nome,
            String codigo,
            int validadeMinutos) {

        enviarCodigo(
                email,
                nome,
                codigo,
                validadeMinutos,
                "Redefinição de senha",
                "Cole o código abaixo no seu aplicativo para criar uma nova senha.",
                "Código para redefinir sua senha - NuPrecin"
        );
    }

    private void enviarCodigo(
            String email,
            String nome,
            String codigo,
            int validadeMinutos,
            String titulo,
            String mensagem,
            String assunto) {

        Context context = new Context();
        context.setVariable("nome", nome);
        context.setVariable("email", email);
        context.setVariable("codigo", codigo);
        context.setVariable("validade", validadeMinutos);
        context.setVariable("titulo", titulo);
        context.setVariable("mensagem", mensagem);

        String html = templateEngine.process(
                "email/cadastro-email",
                context
        );

        if (apiKey.isBlank()) {
            throw new IllegalStateException("RESEND_API_KEY não configurada.");
        }

        try {
            restClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ResendEmailRequest(
                            from,
                            List.of(email),
                            assunto,
                            html
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new IllegalStateException(
                    "Não foi possível enviar o e-mail de código.",
                    ex
            );
        }
    }

    private record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String html
    ) {
    }
}
