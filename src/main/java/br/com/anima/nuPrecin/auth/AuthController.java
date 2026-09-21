package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginRequestDto;
import br.com.anima.nuPrecin.auth.dto.LoginResponseDto;
import br.com.anima.nuPrecin.auth.dto.ConfirmarRegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.MensagemResponseDto;
import br.com.anima.nuPrecin.auth.dto.ReenviarConfirmacaoRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroRequestDto;
import br.com.anima.nuPrecin.auth.dto.RegistroResponseDto;
import br.com.anima.nuPrecin.auth.dto.ResetarSenhaRequestDto;
import br.com.anima.nuPrecin.auth.dto.SolicitarResetSenhaRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Login, criação de conta, confirmação de e-mail e recuperação de senha.")
public class AuthController {
    @Autowired
    private AuthService service;
    @Autowired
    private CadastroService cadastroService;
    @Autowired
    private SenhaService senhaService;

    @PostMapping("login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Valida e-mail e senha e retorna um JWT para as rotas protegidas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponseDto> login (@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok().body(service.login(dto));
    }

    @PostMapping("registro")
    @Operation(
            summary = "Iniciar cadastro",
            description = "Cria um cadastro pendente e envia um código de 4 dígitos por e-mail. A foto é opcional e pode ser enviada depois."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Código enviado",
                    content = @Content(schema = @Schema(implementation = RegistroResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado"),
            @ApiResponse(responseCode = "502", description = "Serviço de e-mail indisponível")
    })
    public ResponseEntity<RegistroResponseDto> iniciarRegistro(
            @Valid @RequestBody RegistroRequestDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(cadastroService.iniciar(dto));
    }

    @PostMapping("registro/confirmar")
    @Operation(
            summary = "Confirmar cadastro",
            description = "Valida o código recebido por e-mail e cria o usuário definitivo. O retorno não contém JWT; faça login depois."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou payload inválido"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
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
    @Operation(
            summary = "Reenviar código de cadastro",
            description = "Gera e envia um novo código para um cadastro pendente. A resposta é genérica mesmo quando não existe cadastro pendente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitação aceita",
                    content = @Content(schema = @Schema(implementation = RegistroResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "E-mail inválido"),
            @ApiResponse(responseCode = "502", description = "Serviço de e-mail indisponível")
    })
    public ResponseEntity<RegistroResponseDto> reenviarConfirmacao(
            @Valid @RequestBody ReenviarConfirmacaoRequestDto dto) {
        return ResponseEntity.accepted().body(cadastroService.reenviar(dto));
    }

    @PostMapping("senha/esqueci")
    @Operation(
            summary = "Solicitar recuperação de senha",
            description = "Envia um código de 4 dígitos para o e-mail. A mensagem é genérica para não revelar se a conta existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitação aceita",
                    content = @Content(schema = @Schema(implementation = MensagemResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "E-mail inválido"),
            @ApiResponse(responseCode = "502", description = "Serviço de e-mail indisponível")
    })
    public ResponseEntity<MensagemResponseDto> solicitarResetSenha(
            @Valid @RequestBody SolicitarResetSenhaRequestDto dto) {
        return ResponseEntity.accepted().body(senhaService.solicitarReset(dto));
    }

    @PostMapping("senha/resetar")
    @Operation(
            summary = "Redefinir senha",
            description = "Valida o código recebido no fluxo de recuperação e grava a nova senha."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada"),
            @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou payload inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> resetarSenha(
            @Valid @RequestBody ResetarSenhaRequestDto dto) {
        senhaService.resetarSenha(dto);
        return ResponseEntity.noContent().build();
    }
}
