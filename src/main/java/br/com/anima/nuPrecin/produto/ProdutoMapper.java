package br.com.anima.nuPrecin.produto;


import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import br.com.anima.nuPrecin.produto.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;


@Mapper(componentModel = "spring")
public interface ProdutoMapper {

     Produto toEntity(ProdutoRequestDto dto);

     ProdutoResponseDto toResponse(Produto entity);

     List<ProdutoResponseDto> toResponseList(List<Produto> entities);

     void updateEntityFromDto(ProdutoRequestDto dto, @MappingTarget Produto entity);
}