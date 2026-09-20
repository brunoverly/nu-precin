package br.com.anima.nuPrecin.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class VerificationChallengeService {

    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private DesafioVerificacaoRepository desafioRepository;

    @Value("${EMAIL_CONFIRMATION_SECRET:${key.token-secret}}")
    private String confirmationSecret;

    @Transactional
    public GeneratedChallenge createRegistrationChallenge(
            CadastroPendente cadastro,
            LocalDateTime expiresAt) {

        desafioRepository.invalidarDesafiosAtivosDoCadastro(
                DesafioTipo.REGISTRO_CONTA,
                cadastro.getId(),
                LocalDateTime.now()
        );

        String code = generateCode();
        DesafioVerificacao desafio = DesafioVerificacao.builder()
                .tipo(DesafioTipo.REGISTRO_CONTA)
                .cadastroPendente(cadastro)
                .emailDestino(cadastro.getEmail())
                .codigoHash(hashCode(DesafioTipo.REGISTRO_CONTA, cadastro.getEmail(), code))
                .tentativas(0)
                .maxTentativas(MAX_ATTEMPTS)
                .criadoEm(LocalDateTime.now())
                .expiraEm(expiresAt)
                .build();

        desafio = desafioRepository.save(desafio);
        return new GeneratedChallenge(desafio, code);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<DesafioVerificacao> validateRegistrationCode(
            CadastroPendente cadastro,
            String code) {

        Optional<DesafioVerificacao> optional = desafioRepository
                .buscarDesafioAtivoDoCadastro(DesafioTipo.REGISTRO_CONTA, cadastro.getId());

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        DesafioVerificacao desafio = optional.get();
        LocalDateTime now = LocalDateTime.now();

        if (desafio.getExpiraEm().isBefore(now)
                || desafio.getTentativas() >= desafio.getMaxTentativas()) {
            desafio.setInvalidadoEm(now);
            desafioRepository.save(desafio);
            return Optional.empty();
        }

        String expectedHash = hashCode(
                DesafioTipo.REGISTRO_CONTA,
                cadastro.getEmail(),
                code
        );

        boolean valid = MessageDigest.isEqual(
                desafio.getCodigoHash().getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8)
        );

        if (!valid) {
            desafio.setTentativas(desafio.getTentativas() + 1);
            if (desafio.getTentativas() >= desafio.getMaxTentativas()) {
                desafio.setInvalidadoEm(now);
            }
            desafioRepository.save(desafio);
            return Optional.empty();
        }

        return Optional.of(desafio);
    }

    private String generateCode() {
        return String.format("%04d", RANDOM.nextInt(10_000));
    }

    private String hashCode(DesafioTipo type, String email, String code) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(
                    confirmationSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(key);
            byte[] digest = mac.doFinal(
                    (type.name() + "|" + email + "|" + code)
                            .getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Não foi possível proteger o código de confirmação.", ex);
        }
    }

    public record GeneratedChallenge(DesafioVerificacao desafio, String code) {
    }
}
