package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
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
@RequestMapping("v1/promocoes")
public class PromocaoController {
    @Autowired
    private PromocaoService service;

    @GetMapping
    public Page<PromocaoResponseDto> findAll(@PageableDefault Pageable pageable,
                                             @RequestParam(required = false) Long idProduto,
                                             @RequestParam(required = false) Long idEstabelecimento,
                                             @RequestParam(required = false) Long idUsuario){
        return service.findAll(pageable,idProduto, idEstabelecimento, idUsuario);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PromocaoResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<PromocaoResponseDto> create(@Valid @RequestBody PromocaoRequestDto dto){
        PromocaoResponseDto response = service.create(dto);
        URI uri =  ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocaoResponseDto> update(@PathVariable Long id, @Valid @RequestBody PromocaoRequestDto dto){
        return ResponseEntity.ok().body(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
