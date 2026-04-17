package br.com.anima.nuPrecin.mapper;

import br.com.anima.nuPrecin.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.dto.ExemploResponseDto;
import br.com.anima.nuPrecin.entity.Exemplo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExemploMapper {

     Exemplo toEntity(ExemploRequestDto dto);

     ExemploResponseDto toResponse(Exemplo entity);

     List<ExemploResponseDto> toResponseList(List<Exemplo> entities);

     void updateEntityFromDto(ExemploRequestDto dto, @MappingTarget Exemplo entity);
}