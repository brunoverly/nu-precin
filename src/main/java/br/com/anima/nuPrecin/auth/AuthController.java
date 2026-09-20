package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginRequestDto;
import br.com.anima.nuPrecin.auth.dto.LoginResponseDto;
import br.com.anima.nuPrecin.auth.dto.ConfirmarRegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.ReenviarConfirmacaoRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroResponseDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("v1/auth")
public class AuthController {
    @Autowired
    private AuthService service;
    @Autowired
    private CadastroService cadastroService;

    @PostMapping("login")
    public ResponseEntity<LoginResponseDto> login (@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok().body(service.login(dto));
    }

    @PostMapping("registro")
    public ResponseEntity<RegistroResponseDto> iniciarRegistro(
            @Valid @RequestBody RegistroRequestDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(cadastroService.iniciar(dto));
    }

    @PostMapping("registro/confirmar")
    public ResponseEntity<UsuarioResponseDto> confirmarRegistro(
            @Valid @RequestBody ConfirmarRegistroRequestDto dto) {
        UsuarioResponseDto usuario = cadastroService.confirmar(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/v1/usuarios/{id}")
                .buildAndExpand(usuario.id())
                .toUri();

        return ResponseEntity.created(location).body(usuario);
    }

    @PostMapping("registro/reenviar")
    public ResponseEntity<RegistroResponseDto> reenviarConfirmacao(
            @Valid @RequestBody ReenviarConfirmacaoRequestDto dto) {
        return ResponseEntity.accepted().body(cadastroService.reenviar(dto));
    }
}
