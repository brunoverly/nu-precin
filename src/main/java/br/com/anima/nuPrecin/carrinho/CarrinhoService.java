package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public CarrinhoResponseDto create(@Valid CarrinhoRequestDto dto) {
        Usuario usuario = buscarUsuario(dto.idUsuario());
        Promocao promocao = buscarPromocao(dto.idPromocao());

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(dto.idUsuario())
                .orElseGet(() -> {
                    Carrinho c = new Carrinho();
                    c.setUsuario(usuario);
                    c.setAtivo(true);
                    return c;
                });

        ItemCarrinho item = carrinhoMapper.toItemEntity(dto);
        item.setPromocao(promocao);
        item.setCarrinho(carrinho);

        carrinho.getItens().add(item);

        carrinhoRepository.save(carrinho);
        return carrinhoMapper.toResponse(carrinho);
    }

    public CarrinhoResponseDto findById(Long id) {
        Carrinho carrinho = carrinhoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("carrinho com id {" + id + "} não localizado no banco"));
        return carrinhoMapper.toResponse(carrinho);
    }

    public CarrinhoResponseDto findByUsuarioId(Long idUsuario) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndAtivoTrue(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("carrinho do usuário com id {" + idUsuario + "} não localizado no banco"));
        return carrinhoMapper.toResponse(carrinho);
    }

    public CarrinhoResponseDto update(Long id, @Valid CarrinhoRequestDto dto) {
        Carrinho carrinho = carrinhoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("carrinho com id {" + id + "} não localizado no banco"));

        Usuario usuario = buscarUsuario(dto.idUsuario());
        Promocao promocao = buscarPromocao(dto.idPromocao());

        carrinhoRepository.findByUsuarioIdAndAtivoTrue(dto.idUsuario())
                .filter(carrinhoExistente -> !carrinhoExistente.getId().equals(id))
                .ifPresent(carrinhoExistente -> {
                    throw new IllegalArgumentException("usuário com id {" + dto.idUsuario() + "} já possui carrinho ativo");
                });

        carrinho.getItens().clear();
        ItemCarrinho item = carrinhoMapper.toItemEntity(dto);
        item.setPromocao(promocao);
        item.setCarrinho(carrinho);
        carrinho.getItens().add(item);

        carrinhoRepository.save(carrinho);
        return carrinhoMapper.toResponse(carrinho);
    }

    public void delete(Long id) {
        Carrinho carrinho = carrinhoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("carrinho com id {" + id + "} não localizado no banco"));

        carrinho.setAtivo(false);
        carrinhoRepository.save(carrinho);
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepository.findByIdAndAtivoTrue(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + idUsuario + "} não localizado no banco"));
    }

    private Promocao buscarPromocao(Long idPromocao) {
        return promocaoRepository.findByIdAndAtivoTrue(idPromocao)
                .orElseThrow(() -> new EntityNotFoundException("promoção com id {" + idPromocao + "} não localizada no banco"));
    }
}