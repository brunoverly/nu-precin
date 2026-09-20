package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CarrinhoService {
    @Autowired
    private CarrinhoRepository carrinhoRepository;
    @Autowired
    private CarrinhoMapper carrinhoMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PromocaoRepository promocaoRepository;
    @Autowired
    private CurrentUserService currentUserService;

    @Transactional
    public CarrinhoResponseDto create(@Valid CarrinhoRequestDto dto) {
        currentUserService.ensureCanUseUserId(dto.idUsuario());
        Usuario usuario = buscarUsuario(dto.idUsuario());
        Promocao promocao = buscarPromocao(dto.idPromocao());

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(dto.idUsuario())
                .orElseGet(() -> {
                    Carrinho c = new Carrinho();
                    c.setUsuario(usuario);
                    c.setAtivo(true);
                    return c;
                });
        carrinho.setAtivo(true);

        ItemCarrinho item = criarItem(dto, promocao, carrinho);

        carrinho.getItens().add(item);
        carrinho.recalcularPrecoTotal();

        carrinhoRepository.save(carrinho);
        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional(readOnly = true)
    public CarrinhoResponseDto findById(Long id) {
        Carrinho carrinho = carrinhoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("carrinho com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(carrinho.getUsuario().getId());
        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional(readOnly = true)
    public CarrinhoResponseDto findByUsuarioId(Long idUsuario) {
        currentUserService.ensureCanManageUser(idUsuario);
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndAtivoTrue(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("carrinho do usuário com id {" + idUsuario + "} não localizado no banco"));
        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponseDto update(Long id, @Valid CarrinhoRequestDto dto) {
        Carrinho carrinho = carrinhoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("carrinho com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(carrinho.getUsuario().getId());
        currentUserService.ensureCanUseUserId(dto.idUsuario());

        Usuario usuario = buscarUsuario(dto.idUsuario());
        Promocao promocao = buscarPromocao(dto.idPromocao());

        carrinhoRepository.findByUsuarioId(dto.idUsuario())
                .filter(carrinhoExistente -> !carrinhoExistente.getId().equals(id))
                .ifPresent(carrinhoExistente -> {
                    throw new IllegalArgumentException("usuário com id {" + dto.idUsuario() + "} já possui carrinho ativo");
                });

        carrinho.setUsuario(usuario);
        carrinho.getItens().clear();
        ItemCarrinho item = criarItem(dto, promocao, carrinho);
        carrinho.getItens().add(item);
        carrinho.recalcularPrecoTotal();

        carrinhoRepository.save(carrinho);
        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public void delete(Long id) {
        Carrinho carrinho = carrinhoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("carrinho com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(carrinho.getUsuario().getId());

        carrinho.setAtivo(false);
        carrinhoRepository.save(carrinho);
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepository.findByIdAndAtivoTrue(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + idUsuario + "} não localizado no banco"));
    }

    private Promocao buscarPromocao(Long idPromocao) {
        Promocao promocao = promocaoRepository.findByIdAndAtivoTrue(idPromocao)
                .orElseThrow(() -> new EntityNotFoundException("promoção com id {" + idPromocao + "} não localizada no banco"));

        LocalDateTime agora = LocalDateTime.now();
        if ((promocao.getDataInicio() != null && agora.isBefore(promocao.getDataInicio()))
                || (promocao.getDataFim() != null && agora.isAfter(promocao.getDataFim()))) {
            throw new IllegalArgumentException("promoção fora do período de validade");
        }

        if (promocao.getPrecoPromocao() == null) {
            throw new IllegalArgumentException("promoção sem preço promocional válido");
        }

        return promocao;
    }

    private ItemCarrinho criarItem(CarrinhoRequestDto dto, Promocao promocao, Carrinho carrinho) {
        ItemCarrinho item = carrinhoMapper.toItemEntity(dto);
        item.setPrecoItem(promocao.getPrecoPromocao());
        item.setPromocao(promocao);
        item.setCarrinho(carrinho);
        item.calcularPrecoTotal();
        return item;
    }
}
