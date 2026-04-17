package br.com.anima.nuPrecin.controller;

import br.com.anima.nuPrecin.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.dto.ExemploResponseDto;
import br.com.anima.nuPrecin.service.ExemploService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exemplos")
public class ExemploController {
    @Autowired
    private ExemploService exemploService;

    @PostMapping
    public ResponseEntity<ExemploResponseDto> create(@RequestBody @Valid ExemploRequestDto dto) {
        return exemploService.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemploResponseDto> findById(@PathVariable Long id) {
        return exemploService.findById(id);
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
        return exemploService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        return exemploService.delete(id);
    }
}

