package br.com.anima.nuPrecin.usuario;

import org.springframework.data.jpa.domain.Specification;

public class UsuarioSpecification {
    public static Specification<Usuario> temNome(String nome) {
        return (root, query, cb) ->
                nome == null || nome.isBlank() ? null :
                        cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Usuario> temEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank() ? null :
                        cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Usuario> ativo() {
        return (root, query, cb) -> cb.equal(root.get("ativo"), true);
    }
}
