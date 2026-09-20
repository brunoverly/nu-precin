package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.MensagemResponseDto;
import br.com.anima.nuPrecin.auth.dto.ResetarSenhaRequestDto;
import br.com.anima.nuPrecin.auth.dto.SolicitarResetSenhaRequestDto;
import br.com.anima.nuPrecin.email.EmailService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class SenhaService {

    private static final int CODE_VALIDITY_MINUTES = 10;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DesafioVerificacaoRepository desafioRepository;
    @Autowired
    private VerificationChallengeService challengeService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Transactional
    public MensagemResponseDto solicitarReset(@Valid SolicitarResetSenhaRequestDto dto) {
        String email = normalizarEmail(dto.email());
        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseAndAtivoTrue(email)
                .orElse(null);

        if (usuario != null) {
            VerificationChallengeService.GeneratedChallenge challenge =
                    challengeService.createUserChallenge(
                            usuario,
                            DesafioTipo.RESET_SENHA,
                            LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES)
                    );

            emailService.enviarCodigoResetSenha(
                    usuario.getEmail(),
                    usuario.getNome(),
                    challenge.code(),
                    CODE_VALIDITY_MINUTES
            );
        }

        return new MensagemResponseDto(
                "Se o e-mail estiver cadastrado, um código será enviado."
        );
    }

    @Transactional
    public void resetarSenha(@Valid ResetarSenhaRequestDto dto) {
        String email = normalizarEmail(dto.email());
        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseAndAtivoTrue(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Código inválido, expirado ou com excesso de tentativas."
                ));

        DesafioVerificacao desafio = challengeService
                .validateUserCode(usuario, DesafioTipo.RESET_SENHA, dto.codigo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Código inválido, expirado ou com excesso de tentativas."
                ));

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);

        desafio.setUsadoEm(LocalDateTime.now());
        desafioRepository.save(desafio);
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
