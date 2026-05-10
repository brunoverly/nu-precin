package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstabelecimentoMapper {
    @Mapping(target = "endereco.id", source = "idEndereco")
    @Mapping(target = "usuario.id", source = "idUsuario")
    Estabelecimento toEntity(EstabelecimentoRequestDto dto);

    @Mapping(target = "idEndereco", source = "endereco.id")
    @Mapping(target = "idUsuario", source = "usuario.id")
    EstabelecimentoResponseDto toResponse(Estabelecimento entity);

    List<EstabelecimentoResponseDto> toResponseList(List<Estabelecimento> entities);

    @Mapping(target = "endereco.id", source = "idEndereco")
    @Mapping(target = "usuario.id", source = "idUsuario")
    void updateEntityFromDto(EstabelecimentoRequestDto dto, @MappingTarget Estabelecimento entity);
}
