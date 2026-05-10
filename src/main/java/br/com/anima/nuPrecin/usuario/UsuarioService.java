package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.usuario.dto.UsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;

    public UsuarioResponseDto create(@Valid UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.save(usuarioMapper.toEntity(dto));
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponseDto findById(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));
        return usuarioMapper.toResponse(usuario);
    }

    public Page<UsuarioResponseDto> findAll(Pageable pageable, String nome, String email) {
        Specification<Usuario> specification = UsuarioSpecification.temNome(nome)
                .and(UsuarioSpecification.temEmail(email))
                .and(UsuarioSpecification.ativo());

        Page<Usuario> usuarios = usuarioRepository.findAll(specification, pageable);
        return usuarios.map(usuarioMapper::toResponse);
    }

    public UsuarioResponseDto update(Long id, @Valid UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));

        usuarioMapper.updateEntityFromDto(dto, usuario);
        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
}
