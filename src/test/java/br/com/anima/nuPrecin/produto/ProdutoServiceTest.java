package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ProdutoMapper produtoMapper;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProdutoService service;

    @Test
    void shouldUpdateExternalImageUrlWhenProvided() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Produto produto = Produto.builder()
                .id(1L)
                .usuario(usuario)
                .imagem("https://storage.example/old.png")
                .build();
        ProdutoRequestDto dto = new ProdutoRequestDto(
                "Coca-Cola 350ml",
                "Refrigerante",
                "Coca-Cola",
                "7894900011517",
                "https://cdn.example/coca.png",
                ProdutoEnum.BEBIDA,
                1L
        );

        when(produtoRepository.findByIdAtivo(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(produtoMapper.toResponse(produto)).thenReturn(
                new ProdutoResponseDto(1L, dto.nome(), dto.descricao(), dto.marca(),
                        dto.codigoDeBarras(), null, dto.imagem(), dto.categoria(), 1L, true)
        );

        service.update(1L, dto);

        assertEquals("https://cdn.example/coca.png", produto.getImagem());
        verify(produtoRepository).save(produto);
    }

    @Test
    void shouldPreserveImageWhenUpdateDoesNotProvideOne() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Produto produto = Produto.builder()
                .id(1L)
                .usuario(usuario)
                .imagem("https://storage.example/current.png")
                .build();
        ProdutoRequestDto dto = new ProdutoRequestDto(
                "Coca-Cola 350ml",
                "Refrigerante",
                "Coca-Cola",
                "7894900011517",
                null,
                ProdutoEnum.BEBIDA,
                1L
        );

        when(produtoRepository.findByIdAtivo(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(produtoMapper.toResponse(produto)).thenReturn(null);

        service.update(1L, dto);

        assertEquals("https://storage.example/current.png", produto.getImagem());
    }
}
