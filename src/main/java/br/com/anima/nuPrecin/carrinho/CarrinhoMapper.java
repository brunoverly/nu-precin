package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrinhoMapper {
    ItemCarrinho toItemEntity(CarrinhoRequestDto dto);

    CarrinhoItemResponseDto toItemResponse(ItemCarrinho entity);

    CarrinhoResponseDto toResponse(Carrinho entity);

    List<CarrinhoResponseDto> toResponseList(List<Carrinho> entities);

    void updateEntityFromDto(CarrinhoRequestDto dto, @MappingTarget ItemCarrinho entity);
}
