package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
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
@RequestMapping("v1/produtos")
@Tag(name = "Produtos", description = "Catálogo de produtos, códigos de barras e imagens.")
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Cria um produto. `imagem` é opcional e pode conter uma URL externa; o arquivo também pode ser enviado depois pelo endpoint multipart.")
    public ResponseEntity<ProdutoResponseDto> create(@RequestBody @Valid ProdutoRequestDto dto) {
        ProdutoResponseDto produtoResponseDto = produtoService.create(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(produtoResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(produtoResponseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(produtoService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Lista produtos ativos com paginação e filtros opcionais por nome, marca e categoria.")
    public Page<ProdutoResponseDto> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria){

        return produtoService.findAll(pageable, nome, marca, categoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados do produto. Se `imagem` não for enviada, a imagem atual é preservada.")
    public ResponseEntity<ProdutoResponseDto> update(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDto dto) {
        return ResponseEntity.ok().body(produtoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar produto", description = "Executa soft delete e marca o produto como inativo.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/{id}/imagem",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Enviar imagem do produto",
            description = "Envia uma imagem no campo multipart `file`. Aceita JPEG, PNG ou WEBP até 5 MB. A URL retornada é salva no campo `imagem` do produto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagem atualizada"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente ou formato inválido"),
            @ApiResponse(responseCode = "413", description = "Arquivo maior que 5 MB"),
            @ApiResponse(responseCode = "502", description = "Storage indisponível")
    })
    public ResponseEntity<ProdutoResponseDto> uploadImagem(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(
                produtoService.uploadImagem(id, file)
        );
    }
}
