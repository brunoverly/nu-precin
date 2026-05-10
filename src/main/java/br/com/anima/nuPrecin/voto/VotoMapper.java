package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VotoMapper {
    @Mapping(target = "usuario.id", source = "idUsuario")
    @Mapping(target = "promocao.id", source = "idPromocao")
    Voto toEntity(VotoRequestDto dto);

    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "idPromocao", source = "promocao.id")
    VotoResponseDto toResponse(Voto entity);

    List<VotoResponseDto> toResponseList(List<Voto> entities);

    @Mapping(target = "usuario.id", source = "idUsuario")
    @Mapping(target = "promocao.id", source = "idPromocao")
    void updateEntityFromDto(VotoRequestDto dto, @MappingTarget Voto entity);
}
