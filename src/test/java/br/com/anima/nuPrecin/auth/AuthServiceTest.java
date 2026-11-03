package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.auth.dto.LoginRequestDto;
import br.com.anima.nuPrecin.security.JwtService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UsuarioRepository repository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService service;

    @Test
    void shouldReturnAuthenticatedUserIdAlongWithExistingFields() {
        Usuario usuario = Usuario.builder()
                .id(42L).nome("Maria").email("maria@example.com").senha("hash").raioBuscaKm(10).build();
        when(repository.findByEmailIgnoreCaseAndAtivoTrue("maria@example.com"))
                .thenReturn(Optional.of(usuario));
        when(encoder.matches("123", "hash")).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("jwt");

        var response = service.login(new LoginRequestDto("Maria@Example.com", "123"));

        assertEquals(42L, response.id());
        assertEquals("Maria", response.nome());
        assertEquals("maria@example.com", response.email());
        assertEquals("jwt", response.token());
        assertEquals(10, response.raioBuscaKm());
    }
}
