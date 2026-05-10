package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.ItemCarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.ItemCarrinhoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemCarrinhoMapper {
    @Mapping(target = "carrinho.id", source = "idCarrinho")
    @Mapping(target = "produto.id", source = "idProduto")
    ItemCarrinho toEntity(ItemCarrinhoRequestDto dto);

    @Mapping(target = "idCarrinho", source = "carrinho.id")
    @Mapping(target = "idProduto", source = "produto.id")
    ItemCarrinhoResponseDto toResponse(ItemCarrinho entity);

    List<ItemCarrinhoResponseDto> toResponseList(List<ItemCarrinho> entities);

    @Mapping(target = "carrinho.id", source = "idCarrinho")
    @Mapping(target = "produto.id", source = "idProduto")
    void updateEntityFromDto(ItemCarrinhoRequestDto dto, @MappingTarget ItemCarrinho entity);
}
