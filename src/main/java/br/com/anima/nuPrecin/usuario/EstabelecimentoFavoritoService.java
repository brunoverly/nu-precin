package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoMapper;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoResponseDto;
import br.com.anima.nuPrecin.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstabelecimentoFavoritoService {
    private final EstabelecimentoFavoritoRepository favoritos;
    private final UsuarioRepository usuarios;
    private final EstabelecimentoRepository estabelecimentos;
    private final EstabelecimentoMapper mapper;
    private final CurrentUserService currentUserService;

    public EstabelecimentoFavoritoService(EstabelecimentoFavoritoRepository favoritos, UsuarioRepository usuarios,
                                         EstabelecimentoRepository estabelecimentos, EstabelecimentoMapper mapper,
                                         CurrentUserService currentUserService) {
        this.favoritos = favoritos;
        this.usuarios = usuarios;
        this.estabelecimentos = estabelecimentos;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    private void validarUsuario(Long id) {
        currentUserService.ensureCanManageUser(id);
        usuarios.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<EstabelecimentoResponseDto> listar(Long id) {
        validarUsuario(id);
        return favoritos.listarAtivos(id).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public void adicionar(Long id, Long idEstabelecimento) {
        validarUsuario(id);
        estabelecimentos.findByIdAndAtivoTrue(idEstabelecimento)
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado."));
        favoritos.adicionarSeAusente(id, idEstabelecimento);
    }

    @Transactional
    public void remover(Long id, Long idEstabelecimento) {
        validarUsuario(id);
        favoritos.remover(id, idEstabelecimento);
    }
}
