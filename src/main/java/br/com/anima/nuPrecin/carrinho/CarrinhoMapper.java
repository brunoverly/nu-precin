package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrinhoMapper {
    @Mapping(target = "usuario.id", source = "idUsuario")
    Carrinho toEntity(CarrinhoRequestDto dto);

    @Mapping(target = "idUsuario", source = "usuario.id")
    CarrinhoResponseDto toResponse(Carrinho entity);

    List<CarrinhoResponseDto> toResponseList(List<Carrinho> entities);

    @Mapping(target = "usuario.id", source = "idUsuario")
    void updateEntityFromDto(CarrinhoRequestDto dto, @MappingTarget Carrinho entity);
}
