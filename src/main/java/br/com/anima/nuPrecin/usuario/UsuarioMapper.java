package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.usuario.dto.UsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toEntity(UsuarioRequestDto dto);

    UsuarioResponseDto toResponse(Usuario entity);

    List<UsuarioResponseDto> toResponseList(List<Usuario> entities);

    void updateEntityFromDto(UsuarioRequestDto dto, @MappingTarget Usuario entity);
}
