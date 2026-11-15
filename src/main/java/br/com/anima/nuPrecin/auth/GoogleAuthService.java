package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginResponseDto;
import br.com.anima.nuPrecin.exception.CredenciaisInvalidasException;
import br.com.anima.nuPrecin.exception.DadosDuplicadosException;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleAuthService {
    private final GoogleTokenService tokens;
    private final UsuarioRepository usuarios;
    private final AuthService auth;
    private final CurrentUserService usuarioAtual;
    private final BCryptPasswordEncoder encoder;
    private final CadastroPendenteRepository cadastros;
    private final DesafioVerificacaoRepository desafios;

    GoogleAuthService(GoogleTokenService tokens, UsuarioRepository usuarios, AuthService auth,
                      CurrentUserService usuarioAtual, BCryptPasswordEncoder encoder,
                      CadastroPendenteRepository cadastros, DesafioVerificacaoRepository desafios) {
        this.tokens = tokens;
        this.usuarios = usuarios;
        this.auth = auth;
        this.usuarioAtual = usuarioAtual;
        this.encoder = encoder;
        this.cadastros = cadastros;
        this.desafios = desafios;
    }

    @Transactional
    public LoginResponseDto login(String idToken) {
        var identidade = tokens.verificar(idToken);
        var vinculo = usuarios.findByGoogleSub(identidade.sub());
        if (vinculo.isPresent()) {
            Usuario usuario = vinculo.get();
            if (!usuario.isAtivo()) {
                throw new CredenciaisInvalidasException("Conta indisponível.");
            }
            return auth.resposta(usuario);
        }

        var existente = usuarios.buscarPorEmailParaVinculo(identidade.email());
        if (existente.isEmpty()) {
            return auth.resposta(criarConta(identidade));
        }
        Usuario usuario = existente.get();
        if (!usuario.isEmailConfirmado() || !identidade.emailSobAutoridadeGoogle()) {
            throw new CredenciaisInvalidasException("Entre na sua conta NuPrecin para vincular o Google.");
        }
        vincular(usuario, identidade.sub());
        return auth.resposta(usuario);
    }

    private Usuario criarConta(GoogleTokenService.Identidade identidade) {
        if (usuarios.existsByEmailIgnoreCase(identidade.email())) {
            throw new CredenciaisInvalidasException("Conta indisponível para cadastro com Google.");
        }
        Usuario usuario = Usuario.builder()
                .nome(identidade.nome() == null ? identidade.email() : identidade.nome())
                .email(identidade.email())
                .emailConfirmado(true)
                .foto(identidade.foto())
                .googleSub(identidade.sub())
                .senha(encoder.encode(UUID.randomUUID().toString()))
                .build();
        usuario = usuarios.saveAndFlush(usuario);
        cadastros.findByEmailIgnoreCaseAndConsumidoEmIsNull(identidade.email()).ifPresent(cadastro -> {
            LocalDateTime agora = LocalDateTime.now();
            cadastro.setConsumidoEm(agora);
            desafios.invalidarDesafiosAtivosDoCadastro(DesafioTipo.REGISTRO_CONTA, cadastro.getId(), agora);
        });
        return usuario;
    }

    @Transactional
    public void vincular(String idToken) {
        Long id = usuarioAtual.getRequiredUser().getId();
        var identidade = tokens.verificar(idToken);
        Usuario usuario = usuarios.buscarAtivoParaAlteracao(id)
                .filter(u -> identidade.email().equalsIgnoreCase(u.getEmail()))
                .orElseThrow(() -> new CredenciaisInvalidasException("O e-mail do Google precisa corresponder ao da sua conta."));
        vincular(usuario, identidade.sub());
    }

    private void vincular(Usuario usuario, String sub) {
        if (sub.equals(usuario.getGoogleSub())) {
            return;
        }
        if (usuario.getGoogleSub() != null || usuarios.findByGoogleSub(sub).isPresent()) {
            throw new DadosDuplicadosException("Já existe um vínculo com uma conta Google.");
        }
        usuario.setGoogleSub(sub);
        usuarios.flush();
    }
}
