package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoProximoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstabelecimentoMapper {

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "endereco", ignore = true)
    Estabelecimento toEntity(EstabelecimentoRequestDto dto);

    @Mapping(target = "idEndereco", source = "endereco.id")
    @Mapping(target = "idUsuario", source = "usuario.id")
    EstabelecimentoResponseDto toResponse(Estabelecimento entity);

    @Mapping(target = "idEndereco", source = "estabelecimento.endereco.id")
    @Mapping(target = "idUsuario", source = "estabelecimento.usuario.id")
    EstabelecimentoProximoResponseDto toProximoResponse(Estabelecimento estabelecimento, double distanciaKm);

    List<EstabelecimentoResponseDto> toResponseList(List<Estabelecimento> entities);


    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "endereco", ignore = true)
    @Mapping(target = "foto", ignore = true)
    void updateEntityFromDto(EstabelecimentoRequestDto dto, @MappingTarget Estabelecimento entity);
}
