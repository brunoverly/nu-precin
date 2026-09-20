package br.com.anima.nuPrecin.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${MAIL_FROM:${SPRING_MAIL_USERNAME}}")
    private String from;

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

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject(assunto);
            helper.setText(
                    "Seu código de confirmação é: " + codigo,
                    html
            );

            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException(
                    "Não foi possível preparar o e-mail de confirmação.",
                    ex
            );
        }
    }
}
