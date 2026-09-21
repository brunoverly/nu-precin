package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.usuario.dto.UsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import br.com.anima.nuPrecin.usuario.dto.AtualizarUsuarioRequestDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("v1/usuarios")
@Tag(name = "Usuários", description = "Conta, perfil, senha e foto do usuário.")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar usuário diretamente", description = "Cria um usuário diretamente. Disponível somente para administradores. Novos usuários recebem role USER. Para cadastro público, use o fluxo de confirmação em `/v1/auth/registro`.")
    public ResponseEntity<UsuarioResponseDto> create(@RequestBody @Valid UsuarioRequestDto dto) {
        UsuarioResponseDto usuarioResponseDto = usuarioService.create(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(usuarioResponseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Usuários comuns só podem consultar o próprio perfil; administradores podem consultar qualquer usuário.")
    public ResponseEntity<UsuarioResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(usuarioService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista usuários ativos com filtros por nome/e-mail. Disponível somente para administradores.")
    public Page<UsuarioResponseDto> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email) {
        return usuarioService.findAll(pageable, nome, email);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza perfil e senha. Exige `senhaAtual` correta e `novaSenha`. Se `foto` não for enviada, a foto atual é preservada.")
    public ResponseEntity<UsuarioResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarUsuarioRequestDto dto) {
        return ResponseEntity.ok().body(usuarioService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário", description = "Executa soft delete e marca o usuário como inativo.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/{id}/foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Enviar foto do usuário",
            description = "Envia uma imagem no campo multipart `file`. Aceita JPEG, PNG ou WEBP até 5 MB. A URL retornada é salva no campo `foto` do usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto atualizada"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente ou formato inválido"),
            @ApiResponse(responseCode = "413", description = "Arquivo maior que 5 MB"),
            @ApiResponse(responseCode = "502", description = "Storage indisponível")
    })
    public ResponseEntity<UsuarioResponseDto> uploadFoto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(
                usuarioService.uploadFoto(id, file)
        );
    }
}
