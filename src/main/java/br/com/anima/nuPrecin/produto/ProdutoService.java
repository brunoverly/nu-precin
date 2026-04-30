package br.com.anima.nuPrecin.produto;


import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoMapper;
import br.com.anima.nuPrecin.produto.ProdutoSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;


@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ProdutoMapper produtoMapper;


    public ResponseEntity<ProdutoResponseDto> create(ProdutoRequestDto dto){
        Produto produto = produtoRepository.save(produtoMapper.toEntity(dto));

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(produto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(produtoMapper.toResponse(produto));
    }

    public ResponseEntity<ProdutoResponseDto> findById(Long id) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("produto com id {"+ id + "} não localizado no banco"));
        return ResponseEntity.ok(produtoMapper.toResponse(produto));
    }

    public Page<ProdutoResponseDto> findAll(Pageable pageable, String nome, String marca, String categoria) {

        Specification<Produto> specification = ProdutoSpecification.temNome(nome)
                .and(ProdutoSpecification.temMarca(marca))
                .and(ProdutoSpecification.temCategoria(categoria))
                .and(ProdutoSpecification.ativo());

        Page<Produto> produtos =  produtoRepository.findAll(specification, pageable);
        Page<ProdutoResponseDto> produtosDto = produtos.map(produtoMapper::toResponse);

        return produtosDto;

    }

    public ResponseEntity<ProdutoResponseDto> update(Long id, @Valid ProdutoRequestDto dto) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));

        produto.setNome(dto.nome());
        produto.setMarca(dto.marca());
        produto.setCategoria(dto.categoria());

        produtoRepository.save(produto);

        return ResponseEntity.ok(produtoMapper.toResponse(produto));

    }

    public ResponseEntity delete(Long id) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));

        produto.setAtivo(false);
        produtoRepository.save(produto);

        return ResponseEntity.noContent().build();

    }
}
