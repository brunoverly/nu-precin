package br.com.anima.nuPrecin.endereco;

import br.com.anima.nuPrecin.endereco.dto.EnderecoRequestDto;
import br.com.anima.nuPrecin.endereco.dto.EnderecoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    Endereco toEntity(EnderecoRequestDto dto);

    EnderecoResponseDto toResponse(Endereco entity);

    List<EnderecoResponseDto> toResponseList(List<Endereco> entities);

    void updateEntityFromDto(EnderecoRequestDto dto, @MappingTarget Endereco entity);
}
