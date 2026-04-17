package br.com.anima.nuPrecin.service;


import br.com.anima.nuPrecin.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.dto.ExemploResponseDto;
import br.com.anima.nuPrecin.entity.Exemplo;
import br.com.anima.nuPrecin.mapper.ExemploMapper;
import br.com.anima.nuPrecin.repository.ExemploRepository;
import br.com.anima.nuPrecin.specification.ExemploSpecification;
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
public class ExemploService {

    @Autowired
    private ExemploRepository exemploRepository;
    @Autowired
    private ExemploMapper exemploMapper;


    public ResponseEntity<ExemploResponseDto> create(ExemploRequestDto dto){
        Exemplo exemplo = exemploRepository.save(exemploMapper.toEntity(dto));

        System.out.println(dto.exemploEnum());
        System.out.println(exemplo.getExemploEnum());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(exemplo.getId())
                .toUri();

        return ResponseEntity.created(uri).body(exemploMapper.toResponse(exemplo));
    }

    public ResponseEntity<ExemploResponseDto> findById(Long id) {
        Exemplo exemplo = exemploRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exemplo com id {"+ id + "} não localizado no banco"));
        return ResponseEntity.ok(exemploMapper.toResponse(exemplo));
    }

    public Page<ExemploResponseDto> findAll(Pageable pageable, String nome, Integer numero, String exemploEnum) {

        Specification<Exemplo> specification = ExemploSpecification.temNome(nome)
                .and(ExemploSpecification.temNumero(numero))
                .and(ExemploSpecification.temExemploEnum(exemploEnum))
                .and(ExemploSpecification.ativo());

        Page<Exemplo> exemplos =  exemploRepository.findAll(specification, pageable);
        Page<ExemploResponseDto> exemplosDto = exemplos.map(exemploMapper::toResponse);

        return exemplosDto;

    }

    public ResponseEntity<ExemploResponseDto> update(Long id, @Valid ExemploRequestDto dto) {
        Exemplo exemplo = exemploRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));

        exemplo.setNome(dto.nome());
        exemplo.setNumero(dto.numero());
        exemplo.setExemploEnum(dto.exemploEnum());

        exemploRepository.save(exemplo);

        return ResponseEntity.ok(exemploMapper.toResponse(exemplo));

    }

    public ResponseEntity delete(Long id) {
        Exemplo exemplo = exemploRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco, ou já consta como desativada"));

        exemplo.setAtivo(false);
        exemploRepository.save(exemplo);

        return ResponseEntity.noContent().build();

    }
}
