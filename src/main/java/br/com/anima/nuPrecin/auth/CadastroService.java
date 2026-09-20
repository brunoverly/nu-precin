package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.ConfirmarRegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.ReenviarConfirmacaoRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroResponseDto;
import br.com.anima.nuPrecin.email.EmailService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioMapper;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import br.com.anima.nuPrecin.exception.DadosDuplicadosException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class CadastroService {

    private static final int CODE_VALIDITY_MINUTES = 10;

    @Autowired
    private CadastroPendenteRepository cadastroRepository;
    @Autowired
    private DesafioVerificacaoRepository desafioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private VerificationChallengeService challengeService;
    @Autowired
    private EmailService emailService;

    @Transactional
    public RegistroResponseDto iniciar(@Valid RegistroRequestDto dto) {
        String email = normalizarEmail(dto.email());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }

        CadastroPendente cadastro = cadastroRepository
                .findByEmailIgnoreCaseAndConsumidoEmIsNull(email)
                .orElseGet(CadastroPendente::new);

        cadastro.setNome(dto.nome());
        cadastro.setEmail(email);
        cadastro.setFoto(dto.foto());
        cadastro.setSenhaHash(passwordEncoder.encode(dto.senha()));
        cadastro.setExpiraEm(LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES));
        cadastro.setConsumidoEm(null);
        cadastro = cadastroRepository.save(cadastro);

        VerificationChallengeService.GeneratedChallenge challenge =
                challengeService.createRegistrationChallenge(
                        cadastro,
                        cadastro.getExpiraEm()
                );

        emailService.enviarCodigoConfirmacao(
                cadastro.getEmail(),
                cadastro.getNome(),
                challenge.code(),
                CODE_VALIDITY_MINUTES
        );

        return new RegistroResponseDto(
                cadastro.getEmail(),
                "Código de confirmação enviado para o e-mail informado."
        );
    }

    @Transactional
    public UsuarioResponseDto confirmar(@Valid ConfirmarRegistroRequestDto dto) {
        String email = normalizarEmail(dto.email());
        CadastroPendente cadastro = cadastroRepository
                .findByEmailIgnoreCaseAndConsumidoEmIsNull(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cadastro pendente não encontrado ou já confirmado."
                ));

        DesafioVerificacao desafio = challengeService
                .validateRegistrationCode(cadastro, dto.codigo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Código inválido, expirado ou com excesso de tentativas."
                ));

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .nome(cadastro.getNome())
                .email(cadastro.getEmail())
                .foto(cadastro.getFoto())
                .senha(cadastro.getSenhaHash())
                .build();

        usuario = usuarioRepository.save(usuario);

        LocalDateTime now = LocalDateTime.now();
        cadastro.setConsumidoEm(now);
        desafio.setUsadoEm(now);
        cadastroRepository.save(cadastro);
        desafioRepository.save(desafio);

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public RegistroResponseDto reenviar(@Valid ReenviarConfirmacaoRequestDto dto) {
        String email = normalizarEmail(dto.email());
        CadastroPendente cadastro = cadastroRepository
                .findByEmailIgnoreCaseAndConsumidoEmIsNull(email)
                .orElse(null);

        if (cadastro != null) {
            cadastro.setExpiraEm(LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES));
            cadastro = cadastroRepository.save(cadastro);

            VerificationChallengeService.GeneratedChallenge challenge =
                    challengeService.createRegistrationChallenge(
                            cadastro,
                            cadastro.getExpiraEm()
                    );

            emailService.enviarCodigoConfirmacao(
                    cadastro.getEmail(),
                    cadastro.getNome(),
                    challenge.code(),
                    CODE_VALIDITY_MINUTES
            );
        }

        return new RegistroResponseDto(
                email,
                "Se houver um cadastro pendente, um novo código será enviado."
        );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
