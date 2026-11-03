package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
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
@RequestMapping("v1/usuarios/{id}/favoritos")
@Tag(name = "Usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioFavoritoController {
    private final EstabelecimentoFavoritoService service;

    public UsuarioFavoritoController(EstabelecimentoFavoritoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar estabelecimentos favoritos", description = "Lista sem paginação, ordenada por nome, com endereco completo e foto de cada estabelecimento ativo. Sem favoritos retorna []. Disponível ao próprio usuário ou ADMIN.")
    public List<EstabelecimentoResponseDto> listar(@PathVariable Long id) {
        return service.listar(id);
    }

    @PutMapping("/{idEstabelecimento}")
    @Operation(summary = "Favoritar estabelecimento", description = "Vincula um estabelecimento ativo ao usuário. Não recebe corpo. Retorna 204, inclusive se já estiver favoritado; não cria duplicatas. Disponível ao próprio usuário ou ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estabelecimento favoritado", content = @Content),
            @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso a favoritos de outro usuário não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário ou estabelecimento ativo não encontrado")
    })
    public ResponseEntity<Void> adicionar(@PathVariable Long id, @PathVariable Long idEstabelecimento) {
        service.adicionar(id, idEstabelecimento);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idEstabelecimento}")
    @Operation(summary = "Remover estabelecimento dos favoritos", description = "Remove somente o vínculo de favorito; não exclui o estabelecimento. Não recebe corpo. Retorna 204, inclusive se o vínculo não existir. Disponível ao próprio usuário ou ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favorito removido", content = @Content),
            @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso a favoritos de outro usuário não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> remover(@PathVariable Long id, @PathVariable Long idEstabelecimento) {
        service.remover(id, idEstabelecimento);
        return ResponseEntity.noContent().build();
    }
}
