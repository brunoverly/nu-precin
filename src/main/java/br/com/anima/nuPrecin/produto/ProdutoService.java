package br.com.anima.nuPrecin.produto;

import org.springframework.web.multipart.MultipartFile;
import br.com.anima.nuPrecin.produto.dto.ComparativoPrecoResponseDto;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.storage.ImageStorageService;
import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import br.com.anima.nuPrecin.produto.dto.ConsultaProdutoResponseDto;
import br.com.anima.nuPrecin.produto.dto.ConsultaProdutoResponseDto.Origem;
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

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ProdutoMapper produtoMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private OpenFoodFactsClient openFoodFacts;
    @Autowired
    private PromocaoRepository promocoes;

    @Transactional(readOnly = true)
    public List<ComparativoPrecoResponseDto> compararPrecos(Long idProduto) {
        produtoRepository.findByIdAtivo(idProduto)
                .orElseThrow(() -> new EntityNotFoundException("Produto ativo não encontrado."));
        var agora = Promocao.agoraNaVigencia();
        return promocoes.compararPrecosVigentes(idProduto, agora, Promocao.limiteConfirmacao(agora));
    }

    @Transactional
    public ProdutoResponseDto create(ProdutoRequestDto dto){
        currentUserService.ensureCanUseUserId(dto.idUsuario());
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        validarEmbalagem(dto);
        Produto produto = produtoMapper.toEntity(dto);
        produto.setCodigoDeBarras(CodigoProduto.normalizar(dto.codigoDeBarras()));
        produto.setQrCode(normalizarQr(dto.qrCode()));
        produto.setUsuario(usuario);
        produto = produtoRepository.save(produto);
        return produtoMapper.toResponse(produto);
    }

    public ConsultaProdutoResponseDto consultarCodigo(String codigo) {
        String normalizado = CodigoProduto.normalizar(codigo);
        var local = produtoRepository.findByCodigoDeBarrasInAndAtivoTrue(CodigoProduto.variantes(normalizado));
        if (local.isPresent()) return resultadoLocal(local.get());

        if (!CodigoProduto.gtinValido(normalizado)) {
            throw new EntityNotFoundException("Código não cadastrado. A consulta externa requer um GTIN válido.");
        }
        var sugestao = openFoodFacts.buscar(normalizado)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado. Use o cadastro manual."));
        return new ConsultaProdutoResponseDto(Origem.OPEN_FOOD_FACTS, null, sugestao);
    }

    public ConsultaProdutoResponseDto consultarQr(String conteudo) {
        String normalizado = normalizarQr(conteudo);
        if (normalizado == null || normalizado.length() > 2048) {
            throw new IllegalArgumentException("Informe o conteúdo do QR Code com até 2048 caracteres.");
        }
        if (normalizado.length() <= 250) {
            var local = produtoRepository.findByQrCodeAndAtivoTrue(normalizado);
            if (local.isPresent()) return resultadoLocal(local.get());
        }
        return consultarCodigo(CodigoProduto.codigoDoQr(normalizado));
    }

    private ConsultaProdutoResponseDto resultadoLocal(Produto produto) {
        return new ConsultaProdutoResponseDto(Origem.LOCAL, produtoMapper.toResponse(produto), null);
    }

    private String normalizarQr(String conteudo) {
        return conteudo == null || conteudo.isBlank() ? null : conteudo.strip();
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDto findById(Long id) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("produto com id {"+ id + "} não localizado no banco"));
        return produtoMapper.toResponse(produto);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDto> findAll(Pageable pageable, String nome, String marca, String categoria) {

        Specification<Produto> specification = ProdutoSpecification.temNome(nome)
                .and(ProdutoSpecification.temMarca(marca))
                .and(ProdutoSpecification.temCategoria(categoria))
                .and(ProdutoSpecification.ativo());

        Page<Produto> produtos =  produtoRepository.findAll(specification, pageable);
        Page<ProdutoResponseDto> produtosDto = produtos.map(produtoMapper::toResponse);

        return produtosDto;

    }

    @Transactional
    public ProdutoResponseDto update(Long id, @Valid ProdutoRequestDto dto) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));
        currentUserService.ensureCanManageUser(produto.getUsuario().getId());
        currentUserService.ensureCanUseUserId(dto.idUsuario());

        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        validarEmbalagem(dto);
        if (dto.quantidadeEmbalagem() != null) {
            produto.setQuantidadeEmbalagem(dto.quantidadeEmbalagem());
            produto.setUnidadeEmbalagem(dto.unidadeEmbalagem());
        }
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setMarca(dto.marca());
        produto.setCodigoDeBarras(CodigoProduto.normalizar(dto.codigoDeBarras()));
        if (dto.qrCode() != null) {
            produto.setQrCode(normalizarQr(dto.qrCode()));
        }
        produto.setCategoria(dto.categoria());
        if (dto.imagem() != null && !dto.imagem().isBlank()) {
            produto.setImagem(dto.imagem());
        }
        produto.setUsuario(usuario);

        produtoRepository.save(produto);

        return produtoMapper.toResponse(produto);

    }

    private void validarEmbalagem(ProdutoRequestDto dto) {
        if ((dto.quantidadeEmbalagem() == null) != (dto.unidadeEmbalagem() == null)) {
            throw new IllegalArgumentException("Informe quantidadeEmbalagem e unidadeEmbalagem juntas.");
        }
    }

    @Transactional
    public void delete(Long id) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException("entidade com o id {" + id + "} não localizada no banco"));
        currentUserService.ensureCanManageUser(produto.getUsuario().getId());

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoResponseDto uploadImagem(Long id, MultipartFile file) {
        Produto produto = produtoRepository.findByIdAtivo(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "produto com id {" + id + "} não localizado no banco"
                ));

        currentUserService.ensureCanManageUser(produto.getUsuario().getId());

        String publicUrl = imageStorageService.upload(
                "produtos",
                id,
                "imagens",
                file
        );

        produto.setImagem(publicUrl);
        produtoRepository.save(produto);

        return produtoMapper.toResponse(produto);
    }
}
