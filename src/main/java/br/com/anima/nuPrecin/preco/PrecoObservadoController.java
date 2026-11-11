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
