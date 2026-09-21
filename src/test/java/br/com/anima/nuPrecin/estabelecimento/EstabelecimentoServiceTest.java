package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.endereco.EnderecoRepository;
import br.com.anima.nuPrecin.estabelecimento.dto.EstabelecimentoRequestDto;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstabelecimentoServiceTest {

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;
    @Mock
    private EnderecoRepository enderecoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EstabelecimentoMapper estabelecimentoMapper;
    @Mock
    private br.com.anima.nuPrecin.storage.ImageStorageService imageStorageService;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private EstabelecimentoService service;

    @Test
    void shouldPreservePhotoWhenUpdateDoesNotProvideOne() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Endereco endereco = Endereco.builder().id(1L).build();
        Estabelecimento estabelecimento = Estabelecimento.builder()
                .id(1L)
                .usuario(usuario)
                .endereco(endereco)
                .foto("https://storage.example/current.jpg")
                .build();
        EstabelecimentoRequestDto dto = new EstabelecimentoRequestDto(
                "SUPERMERCADO",
                "Supermercado BH",
                null,
                "3132224400",
                1L,
                null,
                1L
        );

        when(estabelecimentoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(estabelecimentoRepository.save(estabelecimento)).thenReturn(estabelecimento);
        when(estabelecimentoMapper.toResponse(estabelecimento)).thenReturn(null);

        service.update(1L, dto);

        assertEquals("https://storage.example/current.jpg", estabelecimento.getFoto());
    }

    @Test
    void shouldUpdatePhotoUrlWhenProvided() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Endereco endereco = Endereco.builder().id(1L).build();
        Estabelecimento estabelecimento = Estabelecimento.builder()
                .id(1L)
                .usuario(usuario)
                .endereco(endereco)
                .foto("https://storage.example/old.jpg")
                .build();
        EstabelecimentoRequestDto dto = new EstabelecimentoRequestDto(
                "SUPERMERCADO",
                "Supermercado BH",
                "https://cdn.example/bh.jpg",
                "3132224400",
                1L,
                null,
                1L
        );

        when(estabelecimentoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(estabelecimentoRepository.save(estabelecimento)).thenReturn(estabelecimento);
        when(estabelecimentoMapper.toResponse(estabelecimento)).thenReturn(null);

        service.update(1L, dto);

        assertEquals("https://cdn.example/bh.jpg", estabelecimento.getFoto());
    }
}
