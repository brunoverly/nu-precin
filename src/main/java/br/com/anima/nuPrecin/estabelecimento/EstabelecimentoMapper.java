package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstabelecimentoMapper {

    Estabelecimento toEntity(EstabelecimentoRequestDto dto);

    EstabelecimentoResponseDto toResponse(Estabelecimento entity);

    List<EstabelecimentoResponseDto> toResponseList(List<Estabelecimento> entities);


    void updateEntityFromDto(EstabelecimentoRequestDto dto, @MappingTarget Estabelecimento entity);
}
