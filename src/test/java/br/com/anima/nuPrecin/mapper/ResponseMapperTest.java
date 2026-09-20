package br.com.anima.nuPrecin.mapper;

import br.com.anima.nuPrecin.carrinho.Carrinho;
import br.com.anima.nuPrecin.carrinho.CarrinhoMapper;
import br.com.anima.nuPrecin.carrinho.ItemCarrinho;
import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.estabelecimento.EstabelecimentoMapper;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoMapper;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.voto.Voto;
import br.com.anima.nuPrecin.voto.VotoEnum;
import br.com.anima.nuPrecin.voto.VotoMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseMapperTest {

    private final EstabelecimentoMapper estabelecimentoMapper = Mappers.getMapper(EstabelecimentoMapper.class);
    private final ProdutoMapper produtoMapper = Mappers.getMapper(ProdutoMapper.class);
    private final CarrinhoMapper carrinhoMapper = Mappers.getMapper(CarrinhoMapper.class);
    private final VotoMapper votoMapper = Mappers.getMapper(VotoMapper.class);

    @Test
    void shouldExposeRelatedIdsInEstabelecimentoAndProdutoResponses() {
        Usuario usuario = Usuario.builder().id(3L).build();
        Endereco endereco = Endereco.builder().id(4L).build();
        Estabelecimento estabelecimento = Estabelecimento.builder()
                .id(5L)
                .usuario(usuario)
                .endereco(endereco)
                .build();
        Produto produto = Produto.builder()
                .id(6L)
                .usuario(usuario)
                .build();

        assertEquals(4L, estabelecimentoMapper.toResponse(estabelecimento).idEndereco());
        assertEquals(3L, estabelecimentoMapper.toResponse(estabelecimento).idUsuario());
        assertEquals(3L, produtoMapper.toResponse(produto).idUsuario());
    }

    @Test
    void shouldExposeRelatedIdsInCartAndVoteResponses() {
        Usuario usuario = Usuario.builder().id(7L).build();
        Promocao promocao = Promocao.builder().id(8L).build();
        ItemCarrinho item = ItemCarrinho.builder()
                .id(9L)
                .promocao(promocao)
                .quantidadeItem(1)
                .precoItem(new BigDecimal("2.00"))
                .precoTotal(new BigDecimal("2.00"))
                .build();
        Carrinho carrinho = Carrinho.builder()
                .id(10L)
                .usuario(usuario)
                .itens(List.of(item))
                .build();
        Voto voto = Voto.builder()
                .id(11L)
                .usuario(usuario)
                .promocao(promocao)
                .voto(VotoEnum.POSITIVO)
                .build();

        assertEquals(7L, carrinhoMapper.toResponse(carrinho).idUsuario());
        assertEquals(8L, carrinhoMapper.toResponse(carrinho).itens().get(0).idPromocao());
        assertEquals(7L, votoMapper.toResponse(voto).idUsuario());
        assertEquals(8L, votoMapper.toResponse(voto).idPromocao());
    }
}
