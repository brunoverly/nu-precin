package br.com.anima.nuPrecin.mapper;

import br.com.anima.nuPrecin.dto.ExemploRequestDto;
import br.com.anima.nuPrecin.dto.ExemploResponseDto;
import br.com.anima.nuPrecin.entity.Exemplo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExemploMapper {

     //-> Converte DTO de entrada para entidade
     Exemplo toEntity(ExemploRequestDto dto);

     //-> Converte entidade para DTO de saída
     ExemploResponseDto toResponse(Exemplo entity);

     //-> Converte lista de entidades para lista de DTOs
     List<ExemploResponseDto> toResponseList(List<Exemplo> entities);

     //-> Atualiza uma entidade já existente (PUT / PATCH) com base no DTO
     void updateEntityFromDto(ExemploRequestDto dto, @MappingTarget Exemplo entity);
}