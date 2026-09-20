package br.com.anima.nuPrecin.security;

import br.com.anima.nuPrecin.exception.AcessoNaoAutorizadoException;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowUserToOperateAsItself() {
        authenticate(1L, UsuarioRole.USER, true);

        assertDoesNotThrow(() -> currentUserService.ensureCanUseUserId(1L));
    }

    @Test
    void shouldRejectOperationInNameOfAnotherUser() {
        authenticate(1L, UsuarioRole.USER, true);

        assertThrows(AcessoNaoAutorizadoException.class,
                () -> currentUserService.ensureCanUseUserId(2L));
    }

    @Test
    void shouldRejectInactiveUser() {
        authenticate(1L, UsuarioRole.USER, false);

        assertThrows(AcessoNaoAutorizadoException.class,
                currentUserService::getRequiredUser);
    }

    private void authenticate(Long id, UsuarioRole role, boolean ativo) {
        Usuario usuario = Usuario.builder()
                .id(id)
                .email("usuario@example.com")
                .role(role)
                .ativo(ativo)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
