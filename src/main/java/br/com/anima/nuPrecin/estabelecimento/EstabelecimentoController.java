package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/estabelecimentos")
public class EstabelecimentoController {
    @Autowired
    private EstabelecimentoService estabelecimentoService;

    @PostMapping
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
    public ResponseEntity<EstabelecimentoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(estabelecimentoService.findById(id));
    }

    @GetMapping
    public Page<EstabelecimentoResponseDto> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idUsuario) {
        return estabelecimentoService.findAll(pageable, nome, tipo, idUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDto> update(@PathVariable Long id, @RequestBody @Valid EstabelecimentoRequestDto dto) {
        return ResponseEntity.ok().body(estabelecimentoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estabelecimentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
