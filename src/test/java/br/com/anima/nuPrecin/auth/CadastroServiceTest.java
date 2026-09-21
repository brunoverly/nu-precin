package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.ConfirmarRegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroRequestDto;
import br.com.anima.nuPrecin.email.EmailService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioMapper;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroServiceTest {

    @Mock
    private CadastroPendenteRepository cadastroRepository;
    @Mock
    private DesafioVerificacaoRepository desafioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private VerificationChallengeService challengeService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private CadastroService service;

    @Test
    void shouldCreatePendingRegistrationAndSendConfirmationCode() {
        RegistroRequestDto dto = new RegistroRequestDto(
                "Maria",
                "Maria@Example.com",
                null,
                "123"
        );
        CadastroPendente cadastro = new CadastroPendente();
        cadastro.setId(1L);
        VerificationChallengeService.GeneratedChallenge challenge =
                new VerificationChallengeService.GeneratedChallenge(new DesafioVerificacao(), "4821");

        when(usuarioRepository.existsByEmailIgnoreCase("maria@example.com")).thenReturn(false);
        when(cadastroRepository.findByEmailIgnoreCaseAndConsumidoEmIsNull("maria@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(cadastroRepository.save(any(CadastroPendente.class))).thenAnswer(invocation -> {
            CadastroPendente salvo = invocation.getArgument(0);
            salvo.setId(1L);
            return salvo;
        });
        when(challengeService.createRegistrationChallenge(any(), any())).thenReturn(challenge);

        var response = service.iniciar(dto);

        assertEquals("maria@example.com", response.email());
        verify(emailService).enviarCodigoConfirmacao(
                "maria@example.com", "Maria", "4821", 10
        );
    }

    @Test
    void shouldCreateUserAfterValidatingRegistrationCode() {
        CadastroPendente cadastro = new CadastroPendente();
        cadastro.setId(1L);
        cadastro.setNome("Maria");
        cadastro.setEmail("maria@example.com");
        cadastro.setSenhaHash("hash");
        DesafioVerificacao desafio = new DesafioVerificacao();
        Usuario usuario = Usuario.builder().id(1L).email("maria@example.com").build();

        when(cadastroRepository.findByEmailIgnoreCaseAndConsumidoEmIsNull("maria@example.com"))
                .thenReturn(Optional.of(cadastro));
        when(challengeService.validateRegistrationCode(cadastro, "4821"))
                .thenReturn(Optional.of(desafio));
        when(usuarioRepository.existsByEmailIgnoreCase("maria@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                new UsuarioResponseDto(1L, "Maria", "maria@example.com", null, null)
        );

        UsuarioResponseDto response = service.confirmar(
                new ConfirmarRegistroRequestDto("maria@example.com", "4821")
        );

        assertEquals(1L, response.id());
        verify(cadastroRepository).save(cadastro);
        verify(desafioRepository).save(desafio);
    }
}
