package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.exception.CredenciaisInvalidasException;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.storage.ImageStorageService;
import br.com.anima.nuPrecin.usuario.dto.AtualizarUsuarioRequestDto;
import br.com.anima.nuPrecin.usuario.dto.UsuarioResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private UsuarioService service;

    @Test
    void shouldUpdatePasswordOnlyAfterCheckingCurrentPassword() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("user@example.com")
                .senha("old-hash")
                .foto("https://storage.example/old.jpg")
                .build();
        AtualizarUsuarioRequestDto dto = new AtualizarUsuarioRequestDto(
                "Usuário Atualizado",
                "user@example.com",
                null,
                "senha-atual",
                "senha-nova"
        );

        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-atual", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("senha-nova")).thenReturn("new-hash");
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                new UsuarioResponseDto(1L, "Usuário Atualizado", "user@example.com", usuario.getFoto(), null)
        );

        service.update(1L, dto);

        assertEquals("new-hash", usuario.getSenha());
        assertEquals("https://storage.example/old.jpg", usuario.getFoto());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void shouldRejectUpdateWhenCurrentPasswordIsWrong() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .senha("old-hash")
                .build();
        AtualizarUsuarioRequestDto dto = new AtualizarUsuarioRequestDto(
                "Usuário",
                "user@example.com",
                null,
                "senha-incorreta",
                "senha-nova"
        );

        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-incorreta", "old-hash")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class, () -> service.update(1L, dto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
