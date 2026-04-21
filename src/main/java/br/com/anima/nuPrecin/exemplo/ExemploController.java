package br.com.anima.nuPrecin.exemplo;

import br.com.anima.nuPrecin.exemplo.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.exemplo.dto.ExemploResponseDto;
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
@RequestMapping("/exemplos")
public class ExemploController {
    @Autowired
    private ExemploService exemploService;

    @PostMapping
    public ResponseEntity<ExemploResponseDto> create(@RequestBody @Valid ExemploRequestDto dto) {
        ExemploResponseDto exemploResponseDto = exemploService.create(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(exemploResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(exemploResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemploResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(exemploService.findById(id));
    }

    @GetMapping
    public Page<ExemploResponseDto> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) String exemploEnum){

        return exemploService.findAll(pageable, nome, numero, exemploEnum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExemploResponseDto> update(@PathVariable Long id, @RequestBody @Valid ExemploRequestDto dto) {
        return ResponseEntity.ok().body(exemploService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        exemploService.delete(id);

        return ResponseEntity.noContent().build();
    }
}

