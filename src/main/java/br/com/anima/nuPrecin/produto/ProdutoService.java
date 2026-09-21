package br.com.anima.nuPrecin.produto;

import org.springframework.web.multipart.MultipartFile;
import br.com.anima.nuPrecin.storage.ImageStorageService;
import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
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


    @Transactional
    public ProdutoResponseDto create(ProdutoRequestDto dto){
        currentUserService.ensureCanUseUserId(dto.idUsuario());
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));

        Produto produto = produtoMapper.toEntity(dto);
        produto.setUsuario(usuario);
        produto = produtoRepository.save(produto);
        return produtoMapper.toResponse(produto);
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

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setMarca(dto.marca());
        produto.setCodigoDeBarras(dto.codigoDeBarras());
        produto.setCategoria(dto.categoria());
        if (dto.imagem() != null && !dto.imagem().isBlank()) {
            produto.setImagem(dto.imagem());
        }
        produto.setUsuario(usuario);

        produtoRepository.save(produto);

        return produtoMapper.toResponse(produto);

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
