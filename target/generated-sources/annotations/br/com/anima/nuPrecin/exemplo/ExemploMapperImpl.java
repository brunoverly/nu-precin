package br.com.anima.nuPrecin.exemplo;

import br.com.anima.nuPrecin.exemplo.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.exemplo.dto.ExemploResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-22T19:54:55-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ExemploMapperImpl implements ExemploMapper {

    @Override
    public Exemplo toEntity(ExemploRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Exemplo.ExemploBuilder exemplo = Exemplo.builder();

        exemplo.nome( dto.nome() );
        exemplo.numero( dto.numero() );
        exemplo.exemploEnum( dto.exemploEnum() );

        return exemplo.build();
    }

    @Override
    public ExemploResponseDto toResponse(Exemplo entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nome = null;
        Integer numero = null;
        ExemploEnum exemploEnum = null;
        LocalDateTime dataHorarioCriado = null;

        id = entity.getId();
        nome = entity.getNome();
        numero = entity.getNumero();
        exemploEnum = entity.getExemploEnum();
        dataHorarioCriado = entity.getDataHorarioCriado();

        ExemploResponseDto exemploResponseDto = new ExemploResponseDto( id, nome, numero, exemploEnum, dataHorarioCriado );

        return exemploResponseDto;
    }

    @Override
    public List<ExemploResponseDto> toResponseList(List<Exemplo> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ExemploResponseDto> list = new ArrayList<ExemploResponseDto>( entities.size() );
        for ( Exemplo exemplo : entities ) {
            list.add( toResponse( exemplo ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(ExemploRequestDto dto, Exemplo entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNome( dto.nome() );
        entity.setNumero( dto.numero() );
        entity.setExemploEnum( dto.exemploEnum() );
    }
}
