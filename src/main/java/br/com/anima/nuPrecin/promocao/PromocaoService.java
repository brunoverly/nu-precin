package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PromocaoService {
    @Autowired
    private PromocaoRepository repository;
    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PromocaoMapper mapper;

    public PromocaoResponseDto findById(Long id) {
        Promocao promocao = repository.findByIdAndAtivoTrue(id).orElseThrow(
                ()-> new EntityNotFoundException("Promoção com id {" + id + "} não localizada no sistema."));

        return mapper.toResponse(promocao);
    }

    public PromocaoResponseDto create(@Valid PromocaoRequestDto dto) {
        validarDadosPromocao(dto);

        DependenciasPromocao dependencias = buscarDependencias(dto.codigoBarras(), dto.idEstabelecimento(), dto.idUsuario());

        Promocao promocao = mapper.toEntity(dto);
        promocao.setProduto(dependencias.produto());
        promocao.setEstabelecimento(dependencias.estabelecimento());
        promocao.setUsuario(dependencias.usuario());

        repository.save(promocao);

        return mapper.toResponse(promocao);
    }

    public PromocaoResponseDto update(Long id, @Valid PromocaoRequestDto dto) {
        validarDadosPromocao(dto);

        Promocao promocao = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoção com id{" + id + "} não localizada no sistema."));

        if(dto.precoOriginal() != null) promocao.setPrecoOriginal(dto.precoOriginal());
        if(dto.precoPromocao() != null) promocao.setPrecoPromocao(dto.precoPromocao());
        if(dto.dataInicio() != null) promocao.setDataInicio(dto.dataInicio());
        if(dto.dataFim() != null) promocao.setDataFim(dto.dataFim());

        DependenciasPromocao dependencias = buscarDependencias(dto.codigoBarras(), dto.idEstabelecimento(), dto.idUsuario());
        promocao.setProduto(dependencias.produto());
        promocao.setEstabelecimento(dependencias.estabelecimento());
        promocao.setUsuario(dependencias.usuario());

        promocao.setDataAtualizacao(LocalDateTime.now());
        repository.save(promocao);
        return mapper.toResponse(promocao);
    }

    public void delete(Long id) {
        Promocao promocao = repository.findByIdAndAtivoTrue(id).orElseThrow(
                ()-> new EntityNotFoundException("Promoção com id {" + id + "} não localizada no sistema."));

        promocao.setAtivo(false);
        repository.save(promocao);
    }

    public Page<PromocaoResponseDto> findAll(Pageable pageable, Long idProduto, Long idEstabelecimento, Long idUsuario) {
        Specification<Promocao> specification = PromocaoSpecification.ativo()
                .and(PromocaoSpecification.temProduto(idProduto))
                .and(PromocaoSpecification.temEstabelecimento(idEstabelecimento))
                .and(PromocaoSpecification.temUsuario(idUsuario));

        Page<Promocao> promocoes =  repository.findAll(specification, pageable);
        return promocoes.map(mapper::toResponse);
    }

    private void validarDadosPromocao(PromocaoRequestDto dto) {
        if(dto.precoPromocao().compareTo(dto.precoOriginal())> 0){
            throw new IllegalArgumentException("Preço promocional não pode ser maior que o preço original.");
        }
        if(dto.dataFim().isBefore(dto.dataInicio())){
            throw new IllegalArgumentException("Data de término da promoção deve ser maior que a data de início.");
        }
    }

    private DependenciasPromocao buscarDependencias(String codigoBarras, Long idEstabelecimento, Long idUsuario) {
        Produto produto = produtoRepository.findByCodigoDeBarrasAndAtivoTrue(codigoBarras)
                .orElseThrow(() -> new EntityNotFoundException("produto com código de barras {" + codigoBarras + "} não localizado no banco"));
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(idEstabelecimento)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + idEstabelecimento + "} não localizado no banco"));
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + idUsuario + "} não localizado no banco"));

        return new DependenciasPromocao(produto, estabelecimento, usuario);
    }

    private record DependenciasPromocao(Produto produto, Estabelecimento estabelecimento, Usuario usuario) {
    }
}
