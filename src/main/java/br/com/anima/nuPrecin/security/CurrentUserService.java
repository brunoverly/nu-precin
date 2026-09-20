package br.com.anima.nuPrecin.security;

import br.com.anima.nuPrecin.exception.AcessoNaoAutorizadoException;
import br.com.anima.nuPrecin.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public Usuario getRequiredUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AcessoNaoAutorizadoException("Usuário autenticado não encontrado.");
        }

        if (!usuario.isAtivo()) {
            throw new AcessoNaoAutorizadoException("Usuário inativo.");
        }

        return usuario;
    }

    public void ensureCanUseUserId(Long requestedUserId) {
        Usuario currentUser = getRequiredUser();

        if (requestedUserId == null) {
            throw new AcessoNaoAutorizadoException("Usuário da operação não informado.");
        }

        if (!currentUser.isAdmin() && !currentUser.getId().equals(requestedUserId)) {
            throw new AcessoNaoAutorizadoException("Usuário autenticado não pode operar em nome de outro usuário.");
        }
    }

    public void ensureCanManageUser(Long userId) {
        Usuario currentUser = getRequiredUser();

        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            throw new AcessoNaoAutorizadoException("Usuário autenticado não pode gerenciar este recurso.");
        }
    }

    public boolean isAdmin() {
        return getRequiredUser().isAdmin();
    }
}
