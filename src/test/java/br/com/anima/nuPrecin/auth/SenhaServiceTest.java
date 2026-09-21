package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.ResetarSenhaRequestDto;
import br.com.anima.nuPrecin.auth.dto.SolicitarResetSenhaRequestDto;
import br.com.anima.nuPrecin.email.EmailService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SenhaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private DesafioVerificacaoRepository desafioRepository;
    @Mock
    private VerificationChallengeService challengeService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private SenhaService service;

    @Test
    void shouldSendResetCodeForActiveUser() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("user@example.com")
                .nome("Usuário")
                .build();
        VerificationChallengeService.GeneratedChallenge challenge =
                new VerificationChallengeService.GeneratedChallenge(new DesafioVerificacao(), "4821");

        when(usuarioRepository.findByEmailIgnoreCaseAndAtivoTrue("user@example.com"))
                .thenReturn(Optional.of(usuario));
        when(challengeService.createUserChallenge(any(), any(), any())).thenReturn(challenge);

        service.solicitarReset(new SolicitarResetSenhaRequestDto("USER@example.com"));

        verify(emailService).enviarCodigoResetSenha(
                "user@example.com", "Usuário", "4821", 10
        );
    }

    @Test
    void shouldChangePasswordAfterValidResetCode() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("user@example.com")
                .build();
        DesafioVerificacao desafio = new DesafioVerificacao();

        when(usuarioRepository.findByEmailIgnoreCaseAndAtivoTrue("user@example.com"))
                .thenReturn(Optional.of(usuario));
        when(challengeService.validateUserCode(usuario, DesafioTipo.RESET_SENHA, "4821"))
                .thenReturn(Optional.of(desafio));
        when(passwordEncoder.encode("nova-senha")).thenReturn("new-hash");

        service.resetarSenha(new ResetarSenhaRequestDto(
                "user@example.com", "4821", "nova-senha"
        ));

        verify(usuarioRepository).save(usuario);
        verify(desafioRepository).save(desafio);
    }
}
