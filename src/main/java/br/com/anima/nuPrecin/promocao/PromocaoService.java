package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.preco.PrecoObservadoService;
import br.com.anima.nuPrecin.produto.CodigoProduto;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
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

import java.time.LocalDateTime;
import java.util.Objects;

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
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private PrecoObservadoService historicoPrecos;

    @Transactional(readOnly = true)
    public PromocaoResponseDto findById(Long id) {
        Promocao promocao = repository.findByIdAndAtivoTrue(id).orElseThrow(
                ()-> new EntityNotFoundException("Promoção com id {" + id + "} não localizada no sistema."));

        return mapper.toResponse(promocao);
    }

    @Transactional
    public PromocaoResponseDto create(@Valid PromocaoRequestDto dto) {
        var agora = Promocao.agoraNaVigencia();
        validarDadosPromocao(dto, null, agora);
        currentUserService.ensureCanUseUserId(dto.idUsuario());

        DependenciasPromocao dependencias = buscarDependencias(dto.codigoBarras(), dto.idEstabelecimento(), dto.idUsuario());

        Promocao promocao = mapper.toEntity(dto);
        if (dto.dataFim() == null) {
            promocao.setUltimaConfirmacaoEm(agora);
        }
        vincularDependencias(promocao, dependencias);

        promocao = repository.save(promocao);
        historicoPrecos.registrarPromocao(promocao);

        return mapper.toResponse(promocao);
    }

    @Transactional
    public PromocaoResponseDto update(Long id, @Valid PromocaoRequestDto dto) {
        Promocao promocao = repository.buscarAtivaParaAlteracao(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoção com id{" + id + "} não localizada no sistema."));
        currentUserService.ensureCanManageUser(promocao.getIdUsuario());
        currentUserService.ensureCanUseUserId(dto.idUsuario());

        var agora = Promocao.agoraNaVigencia();
        validarDadosPromocao(dto, promocao.getDataInicio(), agora);
        DependenciasPromocao dependencias = buscarDependencias(dto.codigoBarras(), dto.idEstabelecimento(), dto.idUsuario());
        boolean alterouOferta = promocao.getPrecoOriginal().compareTo(dto.precoOriginal()) != 0
                || promocao.getPrecoPromocao().compareTo(dto.precoPromocao()) != 0
                || !promocao.getDataInicio().equals(dto.dataInicio())
                || !Objects.equals(promocao.getDataFim(), dto.dataFim())
                || promocao.isEnquantoDurarEstoque() != dto.enquantoDurarEstoque()
                || !promocao.getIdProduto().equals(dependencias.produto().getId())
                || !promocao.getIdEstabelecimento().equals(dependencias.estabelecimento().getId());

        promocao.setPrecoOriginal(dto.precoOriginal());
        promocao.setPrecoPromocao(dto.precoPromocao());
        promocao.setDataInicio(dto.dataInicio());
        promocao.setDataFim(dto.dataFim());
        promocao.setEnquantoDurarEstoque(dto.enquantoDurarEstoque());
        if (alterouOferta) {
            promocao.setUltimaConfirmacaoEm(dto.dataFim() == null ? agora : null);
        }
        vincularDependencias(promocao, dependencias);

        promocao.setDataAtualizacao(LocalDateTime.now());
        promocao = repository.save(promocao);
        if (alterouOferta) {
            historicoPrecos.registrarPromocao(promocao);
        }
        return mapper.toResponse(promocao);
    }

    @Transactional
    public PromocaoResponseDto confirmarDisponibilidade(Long id) {
        currentUserService.getRequiredUser();
        Promocao promocao = repository.buscarAtivaParaAlteracao(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoção ativa não encontrada."));
        if (!promocao.isEnquantoDurarEstoque() || promocao.getDataFim() != null) {
            throw new IllegalArgumentException("A confirmação é para promoções de estoque sem data final.");
        }
        promocao.setUltimaConfirmacaoEm(Promocao.agoraNaVigencia());
        return mapper.toResponse(promocao);
    }

    @Transactional
    public void delete(Long id) {
        Promocao promocao = repository.buscarAtivaParaAlteracao(id).orElseThrow(
                ()-> new EntityNotFoundException("Promoção com id {" + id + "} não localizada no sistema."));
        currentUserService.ensureCanManageUser(promocao.getIdUsuario());

        promocao.setAtivo(false);
        repository.save(promocao);
    }

    @Transactional(readOnly = true)
    public Page<PromocaoResponseDto> findAll(Pageable pageable, Long idProduto, Long idEstabelecimento, Long idUsuario) {
        Specification<Promocao> specification = PromocaoSpecification.ativo()
                .and(PromocaoSpecification.temProduto(idProduto))
                .and(PromocaoSpecification.temEstabelecimento(idEstabelecimento))
                .and(PromocaoSpecification.temUsuario(idUsuario));

        Page<Promocao> promocoes =  repository.findAll(specification, pageable);
        return promocoes.map(mapper::toResponse);
    }

    private void validarDadosPromocao(PromocaoRequestDto dto, LocalDateTime inicioAtual, LocalDateTime agora) {
        if (dto.dataFim() == null && !dto.enquantoDurarEstoque()) {
            throw new IllegalArgumentException("Informe dataFim ou marque enquantoDurarEstoque.");
        }
        if (dto.dataFim() == null && dto.dataInicio().isAfter(agora)) {
            throw new IllegalArgumentException("Uma oferta sem data final precisa já ter começado.");
        }
        if (!dto.enquantoDurarEstoque() && !dto.dataInicio().equals(inicioAtual) && !dto.dataInicio().isAfter(agora)) {
            throw new IllegalArgumentException("A data de início de uma nova vigência precisa ser futura.");
        }
        if (dto.dataFim() != null && !dto.dataFim().isAfter(agora)) {
            throw new IllegalArgumentException("A data de término precisa ser futura.");
        }
        if(dto.precoPromocao().compareTo(dto.precoOriginal())> 0){
            throw new IllegalArgumentException("Preço promocional não pode ser maior que o preço original.");
        }
        if(dto.dataFim() != null && dto.dataFim().isBefore(dto.dataInicio())){
            throw new IllegalArgumentException("Data de término da promoção deve ser maior que a data de início.");
        }
    }

    private DependenciasPromocao buscarDependencias(String codigoBarras, Long idEstabelecimento, Long idUsuario) {
        Produto produto = produtoRepository.findByCodigoDeBarrasInAndAtivoTrue(CodigoProduto.variantes(CodigoProduto.normalizar(codigoBarras)))
                .orElseThrow(() -> new EntityNotFoundException("produto com código de barras {" + codigoBarras + "} não localizado no banco"));
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndAtivoTrue(idEstabelecimento)
                .orElseThrow(() -> new EntityNotFoundException("estabelecimento com id {" + idEstabelecimento + "} não localizado no banco"));
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + idUsuario + "} não localizado no banco"));

        return new DependenciasPromocao(produto, estabelecimento, usuario);
    }

    private void vincularDependencias(Promocao promocao, DependenciasPromocao dependencias) {
        promocao.setIdProduto(dependencias.produto().getId());
        promocao.setIdEstabelecimento(dependencias.estabelecimento().getId());
        promocao.setIdUsuario(dependencias.usuario().getId());
        promocao.setProduto(dependencias.produto());
        promocao.setEstabelecimento(dependencias.estabelecimento());
        promocao.setUsuario(dependencias.usuario());
    }

    private record DependenciasPromocao(Produto produto, Estabelecimento estabelecimento, Usuario usuario) {
    }
}
