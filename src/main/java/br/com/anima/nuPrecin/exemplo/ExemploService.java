package br.com.anima.nuPrecin.exemplo;


import br.com.anima.nuPrecin.exemplo.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.exemplo.dto.ExemploResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ExemploService {

    @Autowired
    private ExemploRepository exemploRepository;
    @Autowired
    private ExemploMapper exemploMapper;


    public ExemploResponseDto create(ExemploRequestDto dto){
        Exemplo exemplo = exemploRepository.save(exemploMapper.toEntity(dto));

        return exemploMapper.toResponse(exemplo);
    }

    public ExemploResponseDto findById(Long id) {
        Exemplo exemplo = exemploRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("exemplo com id {"+ id + "} não localizado no banco"));
        return exemploMapper.toResponse(exemplo);
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

    public ExemploResponseDto update(Long id, @Valid ExemploRequestDto dto) {
        Exemplo exemplo = exemploRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));

        exemplo.setNome(dto.nome());
        exemplo.setNumero(dto.numero());
        exemplo.setExemploEnum(dto.exemploEnum());

        exemploRepository.save(exemplo);

        return exemploMapper.toResponse(exemplo);
    }

    public void delete(Long id) {
        Exemplo exemplo = exemploRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));

        exemplo.setAtivo(false);
        exemploRepository.save(exemplo);
    }
}
