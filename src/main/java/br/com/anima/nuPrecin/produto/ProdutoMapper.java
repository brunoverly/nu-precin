package br.com.anima.nuPrecin.produto;


import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;


@Mapper(componentModel = "spring")
public interface ProdutoMapper {

     @Mapping(target = "usuario.id", source = "idUsuario")
     Produto toEntity(ProdutoRequestDto dto);

     @Mapping(target = "idUsuario", source = "usuario.id")
     ProdutoResponseDto toResponse(Produto entity);

     List<ProdutoResponseDto> toResponseList(List<Produto> entities);

     @Mapping(target = "usuario.id", source = "idUsuario")
     void updateEntityFromDto(ProdutoRequestDto dto, @MappingTarget Produto entity);
}
