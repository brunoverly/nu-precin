package br.com.anima.nuPrecin.estabelecimento;

import org.springframework.data.jpa.domain.Specification;

public class EstabelecimentoSpecification {
    public static Specification<Estabelecimento> temNome(String nome) {
        return (root, query, cb) ->
                nome == null || nome.isBlank() ? null :
                        cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Estabelecimento> temTipo(String tipo) {
        return (root, query, cb) ->
                tipo == null || tipo.isBlank() ? null :
                        cb.like(cb.lower(root.get("tipo")), "%" + tipo.toLowerCase() + "%");
    }

    public static Specification<Estabelecimento> temUsuario(Long idUsuario) {
        return (root, query, cb) ->
                idUsuario == null ? null : cb.equal(root.get("usuario").get("id"), idUsuario);
    }

    public static Specification<Estabelecimento> ativo() {
        return (root, query, cb) -> cb.equal(root.get("ativo"), true);
    }
}
