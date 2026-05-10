package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginRequestDto;
import br.com.anima.nuPrecin.auth.dto.LoginResponseDto;
import br.com.anima.nuPrecin.exception.AcessoNaoAutorizadoException;
import br.com.anima.nuPrecin.security.JwtService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;



    public LoginResponseDto login(LoginRequestDto dto){
        Usuario usuario = repository.findByEmail(dto.email())
                .orElse(null);

        if(usuario == null || !encoder.matches(dto.senha(), usuario.getSenha())) {
            throw new AcessoNaoAutorizadoException("Credenciais inválidas");
        }

        return new LoginResponseDto(usuario.getNome(), usuario.getEmail(), jwtService.generateToken(usuario));
    }
}

