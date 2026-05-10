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
    // map request (single item) to ItemCarrinho entity
    @Mapping(target = "promocao.id", source = "idPromocao")
    ItemCarrinho toItemEntity(CarrinhoRequestDto dto);

    // map ItemCarrinho entity to item response dto
    @Mapping(target = "idPromocao", source = "promocao.id")
    CarrinhoItemResponseDto toItemResponse(ItemCarrinho entity);

    // map Carrinho to response, items will be mapped automatically
    @Mapping(target = "itens", source = "itens")
    @Mapping(target = "idUsuario", source = "usuario.id")
    CarrinhoResponseDto toResponse(Carrinho entity);

    List<CarrinhoResponseDto> toResponseList(List<Carrinho> entities);

    // helper to update existing cart by replacing/adding item - handled in service
    void updateEntityFromDto(CarrinhoRequestDto dto, @MappingTarget ItemCarrinho entity);
}
