package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("v1/carrinhos")
public class CarrinhoController {
    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<CarrinhoResponseDto> create(@RequestBody @Valid CarrinhoRequestDto dto) {
        CarrinhoResponseDto carrinhoResponseDto = carrinhoService.create(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(carrinhoResponseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(carrinhoResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrinhoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(carrinhoService.findById(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CarrinhoResponseDto> findByUsuarioId(@PathVariable Long idUsuario) {
        return ResponseEntity.ok().body(carrinhoService.findByUsuarioId(idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarrinhoResponseDto> update(@PathVariable Long id, @RequestBody @Valid CarrinhoRequestDto dto) {
        return ResponseEntity.ok().body(carrinhoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carrinhoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}