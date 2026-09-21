package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("v1/promocoes")
@Tag(name = "Promoções", description = "Preços promocionais vinculados a produtos e estabelecimentos.")
@SecurityRequirement(name = "bearerAuth")
public class PromocaoController {
    @Autowired
    private PromocaoService service;

    @GetMapping
    @Operation(summary = "Listar promoções", description = "Lista promoções ativas com filtros por produto, estabelecimento e usuário.")
    public Page<PromocaoResponseDto> findAll(@PageableDefault Pageable pageable,
                                             @RequestParam(required = false) Long idProduto,
                                             @RequestParam(required = false) Long idEstabelecimento,
                                             @RequestParam(required = false) Long idUsuario){
        return service.findAll(pageable,idProduto, idEstabelecimento, idUsuario);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar promoção por ID")
    public ResponseEntity<PromocaoResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar promoção", description = "Relaciona um produto localizado pelo código de barras a um estabelecimento e registra preço original, preço promocional e período futuro.")
    public ResponseEntity<PromocaoResponseDto> create(@Valid @RequestBody PromocaoRequestDto dto){
        PromocaoResponseDto response = service.create(dto);
        URI uri =  ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar promoção", description = "Atualiza uma promoção ativa. O usuário autenticado precisa ser o proprietário ou administrador.")
    public ResponseEntity<PromocaoResponseDto> update(@PathVariable Long id, @Valid @RequestBody PromocaoRequestDto dto){
        return ResponseEntity.ok().body(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar promoção", description = "Executa soft delete da promoção.")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
