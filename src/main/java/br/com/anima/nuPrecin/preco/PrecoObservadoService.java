package br.com.anima.nuPrecin.preco;

import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.preco.dto.PrecoObservadoRequestDto;
import br.com.anima.nuPrecin.preco.dto.PrecoObservadoResponseDto;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.Instant;

@Service
public class PrecoObservadoService {
    private final PrecoObservadoRepository precos;
    private final ProdutoRepository produtos;
    private final EstabelecimentoRepository estabelecimentos;
    private final CurrentUserService usuarioAtual;

    public PrecoObservadoService(PrecoObservadoRepository precos, ProdutoRepository produtos,
                                  EstabelecimentoRepository estabelecimentos, CurrentUserService usuarioAtual) {
        this.precos = precos;
        this.produtos = produtos;
        this.estabelecimentos = estabelecimentos;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public PrecoObservadoResponseDto registrar(PrecoObservadoRequestDto dto) {
        Long idUsuario = usuarioAtual.getRequiredUser().getId();
        produtos.findByIdAtivo(dto.idProduto())
                .orElseThrow(() -> new EntityNotFoundException("Produto ativo não encontrado."));
        estabelecimentos.findByIdAndAtivoTrue(dto.idEstabelecimento())
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento ativo não encontrado."));

        Instant agora = Instant.now();
        if (dto.observadoEm().isAfter(agora)) {
            throw new IllegalArgumentException("A data da observação não pode estar no futuro.");
        }
        return resposta(precos.save(new PrecoObservado(dto.idProduto(), dto.idEstabelecimento(),
                idUsuario, dto.valor(), dto.observadoEm(), agora)));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void registrarPromocao(Promocao promocao) {
        precos.save(new PrecoObservado(promocao, usuarioAtual.getRequiredUser().getId(), Instant.now()));
    }

    @Transactional(readOnly = true)
    public Page<PrecoObservadoResponseDto> historico(Long idProduto, Long idEstabelecimento, Pageable pageable) {
        if (!produtos.existsById(idProduto)) {
            throw new EntityNotFoundException("Produto não encontrado.");
        }
        Pageable pagina = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if (idEstabelecimento == null) {
            return precos.findByIdProdutoOrderByObservadoEmDescIdDesc(idProduto, pagina).map(this::resposta);
        }
        if (!estabelecimentos.existsById(idEstabelecimento)) {
            throw new EntityNotFoundException("Estabelecimento não encontrado.");
        }
        return precos.findByIdProdutoAndIdEstabelecimentoOrderByObservadoEmDescIdDesc(
                idProduto, idEstabelecimento, pagina).map(this::resposta);
    }

    private PrecoObservadoResponseDto resposta(PrecoObservado preco) {
        return new PrecoObservadoResponseDto(preco.getId(), preco.getIdProduto(), preco.getIdEstabelecimento(),
                preco.getIdUsuario(), preco.getValor(), preco.getObservadoEm(), preco.getRegistradoEm(), preco.getOrigem(),
                preco.getIdPromocao(), preco.getPrecoOriginal(), preco.getInicioVigencia(), preco.getFimVigencia(),
                preco.isEnquantoDurarEstoque());
    }
}
