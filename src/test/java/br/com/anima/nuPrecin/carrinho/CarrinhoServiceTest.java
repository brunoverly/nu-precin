package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.carrinho.dto.CarrinhoRequestDto;
import br.com.anima.nuPrecin.carrinho.dto.CarrinhoResponseDto;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;
    @Mock
    private CarrinhoMapper carrinhoMapper;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PromocaoRepository promocaoRepository;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private CarrinhoService service;

    @Test
    void shouldReactivateCartAndUsePromotionPrice() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Promocao promocao = Promocao.builder()
                .id(2L)
                .precoPromocao(new BigDecimal("12.34"))
                .dataInicio(LocalDateTime.now().minusDays(1))
                .dataFim(LocalDateTime.now().plusDays(1))
                .ativo(true)
                .build();
        Carrinho carrinho = Carrinho.builder()
                .id(3L)
                .usuario(usuario)
                .ativo(false)
                .itens(new ArrayList<>())
                .build();
        ItemCarrinho item = new ItemCarrinho();
        item.setQuantidadeItem(2);
        CarrinhoRequestDto dto = new CarrinhoRequestDto(2L, 2, new BigDecimal("0.01"), 1L);

        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(promocaoRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(promocao));
        when(carrinhoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(carrinho));
        when(carrinhoMapper.toItemEntity(dto)).thenReturn(item);
        when(carrinhoMapper.toResponse(carrinho)).thenReturn(new CarrinhoResponseDto(
                3L, java.util.List.of(), new BigDecimal("24.68"), null, 1L));
        when(carrinhoRepository.save(any(Carrinho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(dto);

        ArgumentCaptor<Carrinho> captor = ArgumentCaptor.forClass(Carrinho.class);
        verify(carrinhoRepository).save(captor.capture());
        Carrinho salvo = captor.getValue();

        assertTrue(salvo.isAtivo());
        assertEquals(1, salvo.getItens().size());
        assertEquals(new BigDecimal("12.34"), salvo.getItens().get(0).getPrecoItem());
        assertEquals(new BigDecimal("24.68"), salvo.getPrecoTotal());
        verify(currentUserService).ensureCanUseUserId(1L);
    }
}
