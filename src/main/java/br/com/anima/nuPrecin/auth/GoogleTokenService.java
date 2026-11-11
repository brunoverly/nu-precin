package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.exception.CredenciaisInvalidasException;
import br.com.anima.nuPrecin.exception.ServicoExternoException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Locale;

@Component
class GoogleTokenService {
    private final GoogleIdTokenVerifier verifier;

    GoogleTokenService(@Value("${app.google.client-id:}") String clientId) {
        verifier = clientId.isBlank() ? null : new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(clientId.trim()))
                .build();
    }

    Identidade verificar(String idToken) {
        if (verifier == null) {
            throw new ServicoExternoException("Login com Google indisponível.", null);
        }

        GoogleIdToken token;
        try {
            token = verifier.verify(idToken);
        } catch (IOException | GeneralSecurityException | IllegalArgumentException ex) {
            throw new CredenciaisInvalidasException("Token do Google inválido.");
        }

        if (token == null) {
            throw new CredenciaisInvalidasException("Token do Google inválido.");
        }
        var payload = token.getPayload();
        String sub = payload.getSubject();
        String email = payload.getEmail();
        if (sub == null || sub.isBlank() || sub.length() > 255 || email == null || email.isBlank()
                || !Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new CredenciaisInvalidasException("Identidade do Google sem e-mail verificado.");
        }
        email = email.toLowerCase(Locale.ROOT);
        String dominioWorkspace = payload.getHostedDomain();
        return new Identidade(sub, email, email.endsWith("@gmail.com")
                || (dominioWorkspace != null && !dominioWorkspace.isBlank()));
    }

    record Identidade(String sub, String email, boolean emailSobAutoridadeGoogle) {}
}
