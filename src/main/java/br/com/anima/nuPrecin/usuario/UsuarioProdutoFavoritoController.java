package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/usuarios/{id}/produtos-favoritos")
@Tag(name = "Usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioProdutoFavoritoController {
    private final ProdutoFavoritoService service;

    public UsuarioProdutoFavoritoController(ProdutoFavoritoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar produtos favoritos", description = "Lista sem paginação, ordenada por nome, com os dados do catálogo de cada produto ativo. Sem favoritos retorna []. Disponível ao próprio usuário ou ADMIN.")
    public List<ProdutoResponseDto> listar(@PathVariable Long id) {
        return service.listar(id);
    }

    @PutMapping("/{idProduto}")
    @Operation(summary = "Favoritar produto", description = "Vincula um produto ativo ao usuário. Não recebe corpo. Retorna 204, inclusive se já estiver favoritado; não cria duplicatas. Disponível ao próprio usuário ou ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto favoritado", content = @Content),
            @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso a favoritos de outro usuário não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário ou produto ativo não encontrado")
    })
    public ResponseEntity<Void> adicionar(@PathVariable Long id, @PathVariable Long idProduto) {
        service.adicionar(id, idProduto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idProduto}")
    @Operation(summary = "Remover produto dos favoritos", description = "Remove somente o vínculo de favorito; não exclui o produto. Não recebe corpo. Retorna 204, inclusive se o vínculo não existir. Disponível ao próprio usuário ou ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favorito removido", content = @Content),
            @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso a favoritos de outro usuário não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> remover(@PathVariable Long id, @PathVariable Long idProduto) {
        service.remover(id, idProduto);
        return ResponseEntity.noContent().build();
    }
}
