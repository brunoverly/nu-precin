package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.endereco.EnderecoRepository;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

    public EstabelecimentoResponseDto create(@Valid EstabelecimentoRequestDto dto) {
        Endereco endereco = enderecoRepository.findById(dto.idEndereco())
                .orElseThrow(() -> new EntityNotFoundException("endereço com id {" + dto.idEndereco() + "} não localizado no banco"));
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        Estabelecimento estabelecimento = estabelecimentoMapper.toEntity(dto);
        estabelecimento.setEndereco(endereco);
        estabelecimento.setUsuario(usuario);
        estabelecimento = estabelecimentoRepository.save(estabelecimento);

        return estabelecimentoMapper.toResponse(estabelecimento);
    }

    public EstabelecimentoResponseDto findById(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + id + "} não localizado no banco"));
        return estabelecimentoMapper.toResponse(estabelecimento);
    }

    public Page<EstabelecimentoResponseDto> findAll(Pageable pageable, String nome, String tipo, Long idUsuario) {
        Specification<Estabelecimento> specification = EstabelecimentoSpecification.temNome(nome)
                .and(EstabelecimentoSpecification.temTipo(tipo))
                .and(EstabelecimentoSpecification.temUsuario(idUsuario))
                .and(EstabelecimentoSpecification.ativo());

        Page<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAll(specification, pageable);
        return estabelecimentos.map(estabelecimentoMapper::toResponse);
    }

    public EstabelecimentoResponseDto update(Long id, @Valid EstabelecimentoRequestDto dto) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + id + "} não localizado no banco"));

        Endereco endereco = enderecoRepository.findById(dto.idEndereco())
                .orElseThrow(() -> new EntityNotFoundException("endereço com id {" + dto.idEndereco() + "} não localizado no banco"));
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        estabelecimentoMapper.updateEntityFromDto(dto, estabelecimento);
        estabelecimento.setEndereco(endereco);
        estabelecimento.setUsuario(usuario);
        estabelecimentoRepository.save(estabelecimento);

        return estabelecimentoMapper.toResponse(estabelecimento);
    }

    public void delete(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + id + "} não localizado no banco"));

        estabelecimento.setAtivo(false);
        estabelecimentoRepository.save(estabelecimento);
    }
}
