package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import br.com.anima.nuPrecin.produto.ProdutoService;
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
@RequestMapping("v1/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDto> create(@RequestBody @Valid ProdutoRequestDto dto) {
        ProdutoResponseDto produtoResponseDto = produtoService.create(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(produtoResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(produtoResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(produtoService.findById(id));
    }

    @GetMapping
    public Page<ProdutoResponseDto> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria){

        return produtoService.findAll(pageable, nome, marca, categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> update(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDto dto) {
        return ResponseEntity.ok().body(produtoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
