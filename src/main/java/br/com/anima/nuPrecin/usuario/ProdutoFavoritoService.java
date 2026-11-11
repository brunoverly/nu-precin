package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.produto.ProdutoMapper;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import br.com.anima.nuPrecin.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoFavoritoService {
    private final ProdutoFavoritoRepository favoritos;
    private final UsuarioRepository usuarios;
    private final ProdutoRepository produtos;
    private final ProdutoMapper mapper;
    private final CurrentUserService currentUserService;

    public ProdutoFavoritoService(ProdutoFavoritoRepository favoritos, UsuarioRepository usuarios,
                                         ProdutoRepository produtos, ProdutoMapper mapper,
                                         CurrentUserService currentUserService) {
        this.favoritos = favoritos;
        this.usuarios = usuarios;
        this.produtos = produtos;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    private void validarUsuario(Long id) {
        currentUserService.ensureCanManageUser(id);
        usuarios.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDto> listar(Long id) {
        validarUsuario(id);
        return favoritos.listarAtivos(id).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public void adicionar(Long id, Long idProduto) {
        validarUsuario(id);
        produtos.findByIdAtivo(idProduto)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
        favoritos.adicionarSeAusente(id, idProduto);
    }

    @Transactional
    public void remover(Long id, Long idProduto) {
        validarUsuario(id);
        favoritos.remover(id, idProduto);
    }
}
