package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("v1/carrinhos")
@Tag(name = "Carrinhos", description = "Carrinho único por usuário e itens baseados em promoções ativas.")
@SecurityRequirement(name = "bearerAuth")
public class CarrinhoController {
    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    @Operation(summary = "Adicionar item ao carrinho", description = "Cria ou reativa o carrinho do usuário e adiciona um item. O preço informado é compatível por contrato, mas o backend usa o preço vigente da promoção.")
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
    @Operation(summary = "Buscar carrinho por ID")
    public ResponseEntity<CarrinhoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(carrinhoService.findById(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Buscar carrinho por usuário")
    public ResponseEntity<CarrinhoResponseDto> findByUsuarioId(@PathVariable Long idUsuario) {
        return ResponseEntity.ok().body(carrinhoService.findByUsuarioId(idUsuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Substituir item do carrinho", description = "Substitui os itens atuais pelo item informado e recalcula o total.")
    public ResponseEntity<CarrinhoResponseDto> update(@PathVariable Long id, @RequestBody @Valid CarrinhoRequestDto dto) {
        return ResponseEntity.ok().body(carrinhoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar carrinho", description = "Executa soft delete do carrinho.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carrinhoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
