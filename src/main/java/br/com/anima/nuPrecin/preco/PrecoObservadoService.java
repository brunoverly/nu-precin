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
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import br.com.anima.nuPrecin.usuario.dto.AutorPublicoDto;

@Service
public class PrecoObservadoService {
    private final PrecoObservadoRepository precos;
    private final ProdutoRepository produtos;
    private final EstabelecimentoRepository estabelecimentos;
    private final CurrentUserService usuarioAtual;
    private final UsuarioRepository usuarios;

    public PrecoObservadoService(PrecoObservadoRepository precos, ProdutoRepository produtos,
                                  EstabelecimentoRepository estabelecimentos, CurrentUserService usuarioAtual, UsuarioRepository usuarios) {
        this.precos = precos;
        this.produtos = produtos;
        this.estabelecimentos = estabelecimentos;
        this.usuarioAtual = usuarioAtual;
        this.usuarios = usuarios;
    }

    @Transactional
    public PrecoObservadoResponseDto registrar(PrecoObservadoRequestDto dto) {
        var usuario = usuarioAtual.getRequiredUser();
        Long idUsuario = usuario.getId();
        produtos.findByIdAtivo(dto.idProduto())
                .orElseThrow(() -> new EntityNotFoundException("Produto ativo não encontrado."));
        estabelecimentos.findByIdAndAtivoTrue(dto.idEstabelecimento())
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento ativo não encontrado."));

        Instant agora = Instant.now();
        if (dto.observadoEm().isAfter(agora)) {
            throw new IllegalArgumentException("A data da observação não pode estar no futuro.");
        }
        return resposta(precos.save(new PrecoObservado(dto.idProduto(), dto.idEstabelecimento(),
                idUsuario, dto.valor(), dto.observadoEm(), agora)),
                new AutorPublicoDto(idUsuario, usuario.getNome(), usuario.getFoto()));
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
            return respostas(precos.findByIdProdutoOrderByObservadoEmDescIdDesc(idProduto, pagina));
        }
        if (!estabelecimentos.existsById(idEstabelecimento)) {
            throw new EntityNotFoundException("Estabelecimento não encontrado.");
        }
        return respostas(precos.findByIdProdutoAndIdEstabelecimentoOrderByObservadoEmDescIdDesc(
                idProduto, idEstabelecimento, pagina));
    }

    @Transactional(readOnly = true)
    public Page<PrecoObservadoResponseDto> listar(Long idProduto, Long idEstabelecimento, Long idUsuario,
                                                 String origem, int page, int size, boolean atuais) {
        validarId(idProduto);
        validarId(idEstabelecimento);
        validarId(idUsuario);
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Informe page >= 0 e size entre 1 e 100.");
        }
        if (origem != null && !Set.of("MANUAL", "PROMOCAO").contains(origem)) {
            throw new IllegalArgumentException("Origem inválida. Use MANUAL ou PROMOCAO.");
        }
        var pagina = PageRequest.of(page, size);
        if (atuais) {
            if (origem != null && !"MANUAL".equals(origem)) {
                throw new IllegalArgumentException("Preços atuais aceitam somente origem MANUAL.");
            }
            Instant agora = Instant.now();
            return respostas(precos.listarAtuais(idProduto, idEstabelecimento, idUsuario,
                    agora.minus(7, ChronoUnit.DAYS), agora, pagina));
        }
        return respostas(precos.listarObservacoes(idProduto, idEstabelecimento, idUsuario, origem, pagina));
    }

    private void validarId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Os filtros de ID precisam ser positivos.");
        }
    }

    private Page<PrecoObservadoResponseDto> respostas(Page<PrecoObservado> pagina) {
        Set<Long> ids = pagina.stream().map(PrecoObservado::getIdUsuario).collect(Collectors.toSet());
        Map<Long, AutorPublicoDto> autores = ids.isEmpty() ? Map.of()
                : usuarios.buscarAutoresPublicos(ids).stream()
                    .collect(Collectors.toMap(AutorPublicoDto::id, autor -> autor));
        return pagina.map(preco -> resposta(preco, autores.getOrDefault(preco.getIdUsuario(),
                new AutorPublicoDto(preco.getIdUsuario(), "Usuário indisponível", null))));
    }

    private PrecoObservadoResponseDto resposta(PrecoObservado preco, AutorPublicoDto autor) {
        return new PrecoObservadoResponseDto(preco.getId(), preco.getIdProduto(), preco.getIdEstabelecimento(),
                preco.getIdUsuario(), preco.getValor(), preco.getObservadoEm(), preco.getRegistradoEm(), preco.getOrigem(),
                preco.getIdPromocao(), preco.getPrecoOriginal(), preco.getInicioVigencia(), preco.getFimVigencia(),
                preco.isEnquantoDurarEstoque(), autor);
    }
}
