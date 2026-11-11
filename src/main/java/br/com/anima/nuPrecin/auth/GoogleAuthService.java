package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginResponseDto;
import br.com.anima.nuPrecin.exception.CredenciaisInvalidasException;
import br.com.anima.nuPrecin.exception.DadosDuplicadosException;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleAuthService {
    private final GoogleTokenService tokens;
    private final UsuarioRepository usuarios;
    private final AuthService auth;
    private final CurrentUserService usuarioAtual;

    GoogleAuthService(GoogleTokenService tokens, UsuarioRepository usuarios, AuthService auth,
                      CurrentUserService usuarioAtual) {
        this.tokens = tokens;
        this.usuarios = usuarios;
        this.auth = auth;
        this.usuarioAtual = usuarioAtual;
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

        Usuario usuario = usuarios.buscarPorEmailParaVinculo(identidade.email())
                .filter(u -> u.isEmailConfirmado() && identidade.emailSobAutoridadeGoogle())
                .orElseThrow(() -> new CredenciaisInvalidasException(
                        "Entre na sua conta NuPrecin para vincular o Google. Se ainda não tiver conta, confirme o cadastro."));
        vincular(usuario, identidade.sub());
        return auth.resposta(usuario);
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
