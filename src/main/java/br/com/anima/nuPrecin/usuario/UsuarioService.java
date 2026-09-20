package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.exception.DadosDuplicadosException;
import br.com.anima.nuPrecin.exception.AcessoNaoAutorizadoException;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.dto.UsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.anima.nuPrecin.storage.ImageStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private ImageStorageService imageStorageService;

    @Transactional
    public UsuarioResponseDto create(@Valid UsuarioRequestDto dto) {
        String email = normalizarEmail(dto.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto findById(Long id) {
        currentUserService.ensureCanManageUser(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDto> findAll(Pageable pageable, String nome, String email) {
        if (!currentUserService.isAdmin()) {
            throw new AcessoNaoAutorizadoException("Somente administradores podem listar usuários.");
        }

        Specification<Usuario> specification = UsuarioSpecification.temNome(nome)
                .and(UsuarioSpecification.temEmail(email))
                .and(UsuarioSpecification.ativo());

        Page<Usuario> usuarios = usuarioRepository.findAll(specification, pageable);
        return usuarios.map(usuarioMapper::toResponse);
    }

    @Transactional
    public UsuarioResponseDto update(Long id, @Valid UsuarioRequestDto dto) {
        currentUserService.ensureCanManageUser(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));

        String email = normalizarEmail(dto.email());
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }

        usuarioMapper.updateEntityFromDto(dto, usuario);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void delete(Long id) {
        currentUserService.ensureCanManageUser(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
    @Transactional
    public UsuarioResponseDto uploadFoto(
            Long id,
            MultipartFile file) {

        currentUserService.ensureCanManageUser(id);

        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "usuário com id {" + id + "} não localizado no banco"
                ));

        String publicUrl = imageStorageService.upload(
                "usuarios",
                id,
                "avatar",
                file
        );

        usuario.setFoto(publicUrl);
        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
