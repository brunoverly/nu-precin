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

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDto> create(@RequestBody @Valid ProdutoRequestDto dto) {
        return produtoService.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> findById(@PathVariable Long id) {
        return produtoService.findById(id);
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
        return produtoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        return produtoService.delete(id);
    }
}

