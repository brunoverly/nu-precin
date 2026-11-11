package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.exception.AcessoNaoAutorizadoException;
import br.com.anima.nuPrecin.exception.CredenciaisInvalidasException;
import br.com.anima.nuPrecin.exception.DadosDuplicadosException;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.dto.AtualizarUsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import br.com.anima.nuPrecin.usuario.dto.PreferenciasUsuarioDto;
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
import java.util.Set;

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
        if (!currentUserService.isAdmin()) {
            throw new AcessoNaoAutorizadoException(
                    "Somente administradores podem criar usuários diretamente.");
        }

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
    public PreferenciasUsuarioDto preferencias(Long id) {
        currentUserService.ensureCanManageUser(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        return new PreferenciasUsuarioDto(usuario.getRaioBuscaKm());
    }

    @Transactional
    public PreferenciasUsuarioDto atualizarPreferencias(Long id, PreferenciasUsuarioDto dto) {
        currentUserService.ensureCanManageUser(id);
        if (dto.raioBuscaKm() == null || !Set.of(1, 3, 5, 10).contains(dto.raioBuscaKm())) {
            throw new IllegalArgumentException("raioBuscaKm deve ser 1, 3, 5 ou 10.");
        }
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        usuario.setRaioBuscaKm(dto.raioBuscaKm());
        usuarioRepository.save(usuario);
        return new PreferenciasUsuarioDto(usuario.getRaioBuscaKm());
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
    public UsuarioResponseDto update(Long id, @Valid AtualizarUsuarioRequestDto dto) {
        currentUserService.ensureCanManageUser(id);
        Usuario usuario = usuarioRepository.buscarAtivoParaAlteracao(id)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + id + "} não localizado no banco"));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha atual inválida.");
        }

        String email = normalizarEmail(dto.email());
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DadosDuplicadosException("E-mail já cadastrado.");
        }

        if (!email.equalsIgnoreCase(usuario.getEmail())) {
            usuario.setEmailConfirmado(false);
        }
        usuarioMapper.updateEntityFromDto(dto, usuario);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        if (dto.foto() != null && !dto.foto().isBlank()) {
            usuario.setFoto(dto.foto());
        }
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
