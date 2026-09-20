package br.com.anima.nuPrecin.promocao;

import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoRepository;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoRepository;
import br.com.anima.nuPrecin.promocao.dto.PromocaoRequestDto;
import br.com.anima.nuPrecin.promocao.dto.PromocaoResponseDto;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromocaoServiceTest {

    @Mock
    private PromocaoRepository repository;
    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PromocaoMapper mapper;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private PromocaoService service;

    @Test
    void shouldPersistAllPromotionForeignKeys() {
        Produto produto = Produto.builder().id(10L).codigoDeBarras("789").build();
        Estabelecimento estabelecimento = Estabelecimento.builder().id(20L).build();
        Usuario usuario = Usuario.builder().id(30L).build();
        Promocao promocao = new Promocao();

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusDays(2);
        PromocaoRequestDto dto = new PromocaoRequestDto(
                new BigDecimal("10.00"),
                new BigDecimal("8.00"),
                inicio,
                fim,
                "789",
                20L,
                30L
        );

        when(produtoRepository.findByCodigoDeBarrasAndAtivoTrue("789"))
                .thenReturn(Optional.of(produto));
        when(estabelecimentoRepository.findByIdAndAtivoTrue(20L))
                .thenReturn(Optional.of(estabelecimento));
        when(usuarioRepository.findByIdAndAtivoTrue(30L))
                .thenReturn(Optional.of(usuario));
        when(mapper.toEntity(dto)).thenReturn(promocao);
        when(mapper.toResponse(promocao)).thenReturn(null);
        when(repository.save(any(Promocao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(dto);

        assertEquals(10L, promocao.getIdProduto());
        assertEquals(20L, promocao.getIdEstabelecimento());
        assertEquals(30L, promocao.getIdUsuario());
        assertEquals(produto, promocao.getProduto());
        assertEquals(estabelecimento, promocao.getEstabelecimento());
        assertEquals(usuario, promocao.getUsuario());
    }
}
