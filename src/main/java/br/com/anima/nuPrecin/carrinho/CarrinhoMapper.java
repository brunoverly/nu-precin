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
    @Mapping(target = "promocao", ignore = true)
    @Mapping(target = "carrinho", ignore = true)
    @Mapping(target = "precoTotal", ignore = true)
    ItemCarrinho toItemEntity(CarrinhoRequestDto dto);

    @Mapping(target = "idPromocao", source = "promocao.id")
    CarrinhoItemResponseDto toItemResponse(ItemCarrinho entity);

    @Mapping(target = "idUsuario", source = "usuario.id")
    CarrinhoResponseDto toResponse(Carrinho entity);

    List<CarrinhoResponseDto> toResponseList(List<Carrinho> entities);

    void updateEntityFromDto(CarrinhoRequestDto dto, @MappingTarget ItemCarrinho entity);
}
