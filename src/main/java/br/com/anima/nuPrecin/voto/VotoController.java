package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/votos")
public class VotoController {
    @Autowired
    private VotoService votoService;

    @PostMapping
    public ResponseEntity<VotoResponseDto> createOrUpdate(@RequestBody @Valid VotoRequestDto dto) {
        VotoResponseDto votoResponseDto = votoService.createOrUpdate(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(votoResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(votoResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(votoService.findById(id));
    }

    @GetMapping
    public List<?> findAll(
            @RequestParam(required = false) Long idPromocao,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) VotoEnum voto,
            @RequestParam(required = false) String agruparPor,
            @RequestParam(required = false) String ordenacao) {
        if ("promocao".equalsIgnoreCase(agruparPor)) {
            return votoService.buscarRankingPromocoes(dataInicio, dataFim, voto, ordenacao);
        }
        return votoService.findAll(idPromocao, idUsuario, dataInicio, dataFim, voto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        votoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
