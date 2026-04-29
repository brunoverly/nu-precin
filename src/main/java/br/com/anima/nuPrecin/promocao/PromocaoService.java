package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
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
//    @Autowired
//    private EstabelecimentoRepository estabelecimentoRepository;
//    @Autowired
//    private ProdutoRepository produtoRepository;
//    @Autowired
//    private UsuarioRepository usuarioRepository;

    @Autowired
    private PromocaoMapper mapper;

    public PromocaoResponseDto findById(Long id) {
        Promocao promocao = repository.findByIdAndAtivoTrue(id).orElseThrow(
                ()-> new EntityNotFoundException("Promoção com id {" + id + "} não localizada no sistema."));

        return mapper.toResponse(promocao);
    }

    public PromocaoResponseDto create(@Valid PromocaoRequestDto dto) {
        validarDadosPromocao(dto);

//        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dto.idEstabelecimento())
//                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento com id{" + dto.idEstabelecimento() +"} não localizado no sistema."));
//        promocao.setEstabelecimento(estabelecimento);
//        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
//                .orElseThrow(() -> new EntityNotFoundException("Usuário com id{" + dto.idUsuario() +"} não localizado no sistema."));
//                        promocao.setUsuario(usuario);
//        Produto produto = produtoRepository.findById(dto.idProduto())
//                .orElseThrow(() -> new EntityNotFoundException("Produto com id{" + dto.idProduto() +"} não localizado no sistema."));
//        promocao.setProduto(produto);

        Promocao promocao = mapper.toEntity(dto);
//        promocao.setEstabelecimento(estabelecimento);
//        promocao.setUsuario(usuario);
//        promocao.setProduto(produto);

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
        if(dto.idEstabelecimento() != null) {
//        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dto.idEstabelecimento())
//                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento com id{" + dto.idEstabelecimento() +"} não localizado no sistema."));
//        promocao.setEstabelecimento(estabelecimento);
        }
        if(dto.idUsuario() != null) {
//        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
//                .orElseThrow(() -> new EntityNotFoundException("Usuário com id{" + dto.idUsuario() +"} não localizado no sistema."));
//                        promocao.setUsuario(usuario);
        }
        if(dto.idProduto() != null) {
//        Produto produto = produtoRepository.findById(dto.idProduto())
//                .orElseThrow(() -> new EntityNotFoundException("Produto com id{" + dto.idProduto() +"} não localizado no sistema."));
//        promocao.setProduto(produto);
        }
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
}
