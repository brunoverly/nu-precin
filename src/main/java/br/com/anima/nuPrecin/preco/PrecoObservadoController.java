package br.com.anima.nuPrecin.preco;

import br.com.anima.nuPrecin.preco.dto.PrecoObservadoRequestDto;
import br.com.anima.nuPrecin.preco.dto.PrecoObservadoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "Preços", description = "Registro e histórico de preços observados.")
@SecurityRequirement(name = "bearerAuth")
public class PrecoObservadoController {
    private final PrecoObservadoService service;

    public PrecoObservadoController(PrecoObservadoService service) {
        this.service = service;
    }

    @PostMapping("/precos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar um preço observado",
            description = "Acrescenta uma observação em reais para um produto e uma loja ativos. O autor vem do JWT.")
    @ApiResponse(responseCode = "201", description = "Preço registrado")
    @ApiResponse(responseCode = "400", description = "Observação inválida")
    @ApiResponse(responseCode = "404", description = "Produto ou estabelecimento ativo não encontrado")
    public PrecoObservadoResponseDto registrar(@Valid @RequestBody PrecoObservadoRequestDto dto) {
        return service.registrar(dto);
    }

    @GetMapping("/precos")
    @Operation(summary = "Consultar observações comunitárias de preços",
            description = "Histórico geral, incluindo registros MANUAL e PROMOCAO, disponível a qualquer usuário autenticado. Filtros por igualdade. Inclui produtos e lojas inativos. Ordenação fixa por observadoEm e id decrescentes. page inicia em 0 e size aceita 1 a 100. IDs inexistentes retornam lista vazia.")
    @ApiResponse(responseCode = "200", description = "Lista paginada, inclusive vazia")
    @ApiResponse(responseCode = "400", description = "Filtro ou paginação inválidos")
    @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido")
    public Page<PrecoObservadoResponseDto> listar(
            @RequestParam(required = false) Long idProduto,
            @RequestParam(required = false) Long idEstabelecimento,
            @RequestParam(required = false) Long idUsuario,
            @Parameter(schema = @Schema(allowableValues = {"MANUAL", "PROMOCAO"}))
            @RequestParam(required = false) String origem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listar(idProduto, idEstabelecimento, idUsuario, origem, page, size, false);
    }

    @GetMapping("/precos/atuais")
    @Operation(summary = "Consultar preços comuns de referência para o catálogo",
            description = "Uma observação MANUAL por produto e loja ativos, observada nos últimos 7 dias (168 horas). Seleciona maior observadoEm e desempata por maior id. idUsuario filtra o autor da observação vencedora. Exclui snapshots de promoções. Não altera o comparativo de promoções. Ordenação fixa por observadoEm e id decrescentes. page inicia em 0 e size aceita 1 a 100. Ignora sort. IDs inexistentes retornam lista vazia.")
    @ApiResponse(responseCode = "200", description = "Lista paginada, inclusive vazia")
    @ApiResponse(responseCode = "400", description = "Filtro ou paginação inválidos")
    @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido")
    public Page<PrecoObservadoResponseDto> atuais(
            @RequestParam(required = false) Long idProduto,
            @RequestParam(required = false) Long idEstabelecimento,
            @RequestParam(required = false) Long idUsuario,
            @Parameter(schema = @Schema(allowableValues = {"MANUAL"}))
            @RequestParam(required = false) String origem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listar(idProduto, idEstabelecimento, idUsuario, origem, page, size, true);
    }

    @GetMapping("/produtos/{idProduto}/historico-precos")
    @Operation(summary = "Consultar histórico de preços de um produto",
            description = "Lista paginada com ordenação fixa pela data da observação e pelo ID, ambos decrescentes. Permite filtrar por estabelecimento.",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20"))
            })
    @ApiResponse(responseCode = "200", description = "Histórico paginado")
    @ApiResponse(responseCode = "404", description = "Produto ou estabelecimento não encontrado")
    public Page<PrecoObservadoResponseDto> historico(@PathVariable Long idProduto,
            @RequestParam(required = false) Long idEstabelecimento,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable) {
        return service.historico(idProduto, idEstabelecimento, pageable);
    }
}
