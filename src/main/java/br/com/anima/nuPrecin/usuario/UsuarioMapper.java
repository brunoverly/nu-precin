package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.usuario.dto.UsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import br.com.anima.nuPrecin.usuario.dto.AtualizarUsuarioRequestDto;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDto dto);

    UsuarioResponseDto toResponse(Usuario entity);

    List<UsuarioResponseDto> toResponseList(List<Usuario> entities);

    void updateEntityFromDto(UsuarioRequestDto dto, @MappingTarget Usuario entity);

    @Mapping(target = "foto", ignore = true)
    @Mapping(target = "senha", ignore = true)
    void updateEntityFromDto(AtualizarUsuarioRequestDto dto, @MappingTarget Usuario entity);
}
