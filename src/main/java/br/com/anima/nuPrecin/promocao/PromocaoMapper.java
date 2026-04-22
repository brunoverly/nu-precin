package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {
    Promocao toEntity(PromocaoRequestDto dto);
    PromocaoResponseDto toResponse(Promocao entity);
    List<PromocaoResponseDto> toResponseList(List<Promocao> entity);

}
