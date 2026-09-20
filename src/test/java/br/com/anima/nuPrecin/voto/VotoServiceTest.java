package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;
    @Mock
    private VotoMapper votoMapper;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PromocaoRepository promocaoRepository;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private VotoService service;

    @Test
    void shouldMarkNewVoteAsCreated() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Promocao promocao = Promocao.builder().id(2L).idUsuario(3L).build();
        VotoRequestDto dto = new VotoRequestDto(VotoEnum.POSITIVO, 1L, 2L);

        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(promocaoRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(promocao));
        when(votoRepository.findByUsuarioIdAndPromocaoIdAndAtivoTrue(1L, 2L))
                .thenReturn(Optional.empty());
        when(votoMapper.toResponse(any(Voto.class)))
                .thenReturn(new VotoResponseDto(10L, VotoEnum.POSITIVO, null, 1L, 2L));

        VotoService.VotoOperationResult result = service.createOrUpdate(dto);

        assertTrue(result.created());
    }

    @Test
    void shouldMarkExistingVoteAsUpdated() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Promocao promocao = Promocao.builder().id(2L).idUsuario(3L).build();
        Voto voto = Voto.builder().id(10L).usuario(usuario).promocao(promocao).build();
        VotoRequestDto dto = new VotoRequestDto(VotoEnum.NEGATIVO, 1L, 2L);

        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(promocaoRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(promocao));
        when(votoRepository.findByUsuarioIdAndPromocaoIdAndAtivoTrue(1L, 2L))
                .thenReturn(Optional.of(voto));
        when(votoMapper.toResponse(voto))
                .thenReturn(new VotoResponseDto(10L, VotoEnum.NEGATIVO, null, 1L, 2L));

        VotoService.VotoOperationResult result = service.createOrUpdate(dto);

        assertFalse(result.created());
    }
}
