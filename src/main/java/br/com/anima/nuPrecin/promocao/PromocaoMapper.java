package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    @Mapping(target = "idProduto", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "estabelecimento", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Promocao toEntity(PromocaoRequestDto dto);

    PromocaoResponseDto toResponse(Promocao entity);

    List<PromocaoResponseDto> toResponseList(List<Promocao> entity);

}
