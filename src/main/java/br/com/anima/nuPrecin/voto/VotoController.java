package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping({"/v1/votos", "/votos"})
@Tag(name = "Votos", description = "Votos em promoções, filtros e ranking.")
@SecurityRequirement(name = "bearerAuth")
public class VotoController {
    @Autowired
    private VotoService votoService;

    @PostMapping
    @Operation(summary = "Criar ou atualizar voto", description = "Cria um voto ou atualiza o voto ativo do usuário para a promoção. Um usuário não pode votar na própria promoção.")
    public ResponseEntity<VotoResponseDto> createOrUpdate(@RequestBody @Valid VotoRequestDto dto) {
        VotoService.VotoOperationResult operation = votoService.createOrUpdate(dto);
        VotoResponseDto votoResponseDto = operation.response();

        if (!operation.created()) {
            return ResponseEntity.ok(votoResponseDto);
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(votoResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(votoResponseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar voto por ID")
    public ResponseEntity<VotoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(votoService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar votos ou consultar ranking", description = "Sem `agruparPor`, retorna votos paginados. Com `agruparPor=promocao`, exige dataInicio/dataFim e retorna o ranking de promoções.")
    public ResponseEntity<?> findAll(
            @PageableDefault(size = 20, sort = "dataVoto", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long idPromocao,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) VotoEnum voto,
            @RequestParam(required = false) String agruparPor,
            @RequestParam(required = false) String ordenacao) {
        if ("promocao".equalsIgnoreCase(agruparPor)) {
            return ResponseEntity.ok(votoService.buscarRankingPromocoes(dataInicio, dataFim, voto, ordenacao));
        }
        if (agruparPor != null && !agruparPor.isBlank()) {
            throw new IllegalArgumentException("agruparPor deve ser promocao.");
        }
        return ResponseEntity.ok(votoService.findAll(idPromocao, idUsuario, dataInicio, dataFim, voto, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar voto", description = "Executa soft delete do voto.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        votoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
