package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VotoMapper {

    Voto toEntity(VotoRequestDto dto);

    VotoResponseDto toResponse(Voto entity);

    List<VotoResponseDto> toResponseList(List<Voto> entities);

    void updateEntityFromDto(VotoRequestDto dto, @MappingTarget Voto entity);
}
