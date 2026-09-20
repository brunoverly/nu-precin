package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginRequestDto;
import br.com.anima.nuPrecin.auth.dto.LoginResponseDto;
import br.com.anima.nuPrecin.exception.CredenciaisInvalidasException;
import br.com.anima.nuPrecin.security.JwtService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;



    public LoginResponseDto login(LoginRequestDto dto){
        String email = dto.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = repository.findByEmailIgnoreCaseAndAtivoTrue(email)
                .orElse(null);

        if(usuario == null || !encoder.matches(dto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }

        return new LoginResponseDto(usuario.getNome(), usuario.getEmail(), jwtService.generateToken(usuario));
    }
}
