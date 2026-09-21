package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.endereco.EnderecoRepository;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
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

@Service
public class EstabelecimentoService {
    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EstabelecimentoMapper estabelecimentoMapper;
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private CurrentUserService currentUserService;

    @Transactional
    public EstabelecimentoResponseDto create(@Valid EstabelecimentoRequestDto dto) {
        currentUserService.ensureCanUseUserId(dto.idUsuario());

        // resolve usuario
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        // resolve endereco: prefer idEndereco se informado; senão use endereco embutido
        if (dto.idEndereco() != null && dto.endereco() != null) {
            throw new IllegalArgumentException("Informe apenas idEndereco ou endereco, não os dois.");
        }

        Endereco endereco = null;
        if (dto.idEndereco() != null) {
            endereco = enderecoRepository.findById(dto.idEndereco())
                    .orElseThrow(() -> new EntityNotFoundException("endereço com id {" + dto.idEndereco() + "} não localizado no banco"));
        } else if (dto.endereco() != null) {
            Endereco novo = Endereco.builder()
                    .logradouro(dto.endereco().logradouro())
                    .bairro(dto.endereco().bairro())
                    .cidade(dto.endereco().cidade())
                    .estado(dto.endereco().estado())
                    .build();
            endereco = enderecoRepository.save(novo);
        } else {
            throw new IllegalArgumentException("Informe idEndereco ou o objeto endereco no payload");
        }

        Estabelecimento estabelecimento = estabelecimentoMapper.toEntity(dto);
        estabelecimento.setEndereco(endereco);
        estabelecimento.setUsuario(usuario);
        estabelecimento = estabelecimentoRepository.save(estabelecimento);

        return estabelecimentoMapper.toResponse(estabelecimento);
    }

    @Transactional(readOnly = true)
    public EstabelecimentoResponseDto findById(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + id + "} não localizado no banco"));
        return estabelecimentoMapper.toResponse(estabelecimento);
    }

    @Transactional(readOnly = true)
    public Page<EstabelecimentoResponseDto> findAll(Pageable pageable, String nome, String tipo, Long idUsuario) {
        Specification<Estabelecimento> specification = EstabelecimentoSpecification.temNome(nome)
                .and(EstabelecimentoSpecification.temTipo(tipo))
                .and(EstabelecimentoSpecification.temUsuario(idUsuario))
                .and(EstabelecimentoSpecification.ativo());

        Page<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAll(specification, pageable);
        return estabelecimentos.map(estabelecimentoMapper::toResponse);
    }

    @Transactional
    public EstabelecimentoResponseDto update(Long id, @Valid EstabelecimentoRequestDto dto) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(estabelecimento.getUsuario().getId());
        currentUserService.ensureCanUseUserId(dto.idUsuario());

        if (dto.idEndereco() != null && dto.endereco() != null) {
            throw new IllegalArgumentException("Informe apenas idEndereco ou endereco, não os dois.");
        }

        // resolve usuario
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        // resolve endereco: id takes precedence, else embedded endereco; if embedded, update existing endereco or create new
        Endereco endereco = null;
        if (dto.idEndereco() != null) {
            endereco = enderecoRepository.findById(dto.idEndereco())
                    .orElseThrow(() -> new EntityNotFoundException("endereço com id {" + dto.idEndereco() + "} não localizado no banco"));
        } else if (dto.endereco() != null) {
            // if estabelecimento already has endereco, update its fields; else create new
            if (estabelecimento.getEndereco() != null) {
                Endereco eExist = estabelecimento.getEndereco();
                eExist.setLogradouro(dto.endereco().logradouro());
                eExist.setBairro(dto.endereco().bairro());
                eExist.setCidade(dto.endereco().cidade());
                eExist.setEstado(dto.endereco().estado());
                endereco = enderecoRepository.save(eExist);
            } else {
                Endereco novo = Endereco.builder()
                        .logradouro(dto.endereco().logradouro())
                        .bairro(dto.endereco().bairro())
                        .cidade(dto.endereco().cidade())
                        .estado(dto.endereco().estado())
                        .build();
                endereco = enderecoRepository.save(novo);
            }
        } else {
            throw new IllegalArgumentException("Informe idEndereco ou o objeto endereco no payload");
        }

        estabelecimentoMapper.updateEntityFromDto(dto, estabelecimento);
        if (dto.foto() != null && !dto.foto().isBlank()) {
            estabelecimento.setFoto(dto.foto());
        }
        estabelecimento.setEndereco(endereco);
        estabelecimento.setUsuario(usuario);
        estabelecimentoRepository.save(estabelecimento);

        return estabelecimentoMapper.toResponse(estabelecimento);
    }

    @Transactional
    public void delete(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(estabelecimento.getUsuario().getId());

        estabelecimento.setAtivo(false);
        estabelecimentoRepository.save(estabelecimento);
    }

    @Transactional
    public EstabelecimentoResponseDto uploadFoto(
            Long id,
            MultipartFile file) {

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "estabelecimento com id {" + id + "} não localizado no banco"
                ));

        currentUserService.ensureCanManageUser(
                estabelecimento.getUsuario().getId()
        );

        String publicUrl = imageStorageService.upload(
                "estabelecimentos",
                id,
                "fotos",
                file
        );

        estabelecimento.setFoto(publicUrl);
        estabelecimentoRepository.save(estabelecimento);

        return estabelecimentoMapper.toResponse(estabelecimento);
    }
}
