package br.com.anima.nuPrecin.security;

import br.com.anima.nuPrecin.usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class JwtService {
    //chaves secretas, salvas no application.properties-dev
    @Value("${key.token-secret}")
    private String tokenSecret;
    @Value("${key.token-issuer}")
    private String tokenIssuer;

    //criar um bearer token
    public String generateToken(Usuario usuario){
        try{
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
            return JWT.create()
                    .withIssuer(tokenIssuer)
                    .withSubject(usuario.getId().toString())
                    .withClaim("role", usuario.getRole().name())
                    .withExpiresAt(generateExpiresDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao tentar criar o bearer token");
        }
    }

    public Optional<Long> usuarioId(String token) {
        try {
            String subject = JWT.require(Algorithm.HMAC256(tokenSecret))
                    .withIssuer(tokenIssuer)
                    .withClaimPresence("sub")
                    .withClaimPresence("exp")
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.of(Long.parseLong(subject));
        } catch (JWTVerificationException | NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Instant generateExpiresDate() {
        return Instant.now().plus(24, ChronoUnit.HOURS);
    }
}
