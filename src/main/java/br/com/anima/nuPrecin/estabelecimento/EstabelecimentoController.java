package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
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
@RequestMapping("v1/estabelecimentos")
@Tag(name = "Estabelecimentos", description = "Cadastro, consulta, atualização e fotos de estabelecimentos.")
@SecurityRequirement(name = "bearerAuth")
public class EstabelecimentoController {
    @Autowired
    private EstabelecimentoService estabelecimentoService;

    @PostMapping
    @Operation(
            summary = "Cadastrar estabelecimento",
            description = "Cria um estabelecimento. Informe exatamente um endereço: `idEndereco` ou o objeto `endereco`. A foto pode ser uma URL ou ser enviada depois pelo endpoint multipart."
    )
    public ResponseEntity<EstabelecimentoResponseDto> create(@RequestBody @Valid EstabelecimentoRequestDto dto) {
        EstabelecimentoResponseDto estabelecimentoResponseDto = estabelecimentoService.create(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(estabelecimentoResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(estabelecimentoResponseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estabelecimento por ID")
    public ResponseEntity<EstabelecimentoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(estabelecimentoService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar estabelecimentos", description = "Lista estabelecimentos ativos com paginação e filtros opcionais por nome, tipo e usuário criador.")
    public Page<EstabelecimentoResponseDto> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idUsuario) {
        return estabelecimentoService.findAll(pageable, nome, tipo, idUsuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estabelecimento", description = "Atualiza os dados do estabelecimento. Se `foto` não for enviada, a foto atual é preservada.")
    public ResponseEntity<EstabelecimentoResponseDto> update(@PathVariable Long id, @RequestBody @Valid EstabelecimentoRequestDto dto) {
        return ResponseEntity.ok().body(estabelecimentoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar estabelecimento", description = "Executa soft delete e marca o estabelecimento como inativo.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estabelecimentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/{id}/foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Enviar foto do estabelecimento",
            description = "Envia uma imagem no campo multipart `file`. Aceita JPEG, PNG ou WEBP até 5 MB. A URL retornada é salva no campo `foto` do estabelecimento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto atualizada"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente ou formato inválido"),
            @ApiResponse(responseCode = "413", description = "Arquivo maior que 5 MB"),
            @ApiResponse(responseCode = "502", description = "Storage indisponível")
    })
    public ResponseEntity<EstabelecimentoResponseDto> uploadFoto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(
                estabelecimentoService.uploadFoto(id, file)
        );
    }
}
