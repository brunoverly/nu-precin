package br.com.anima.nuPrecin.exemplo;

import org.springframework.data.jpa.domain.Specification;

public class ExemploSpecification {
    public static Specification<Exemplo> temNome(String nome) {
        return (root, query, cb) ->
                nome == null|| nome.isBlank() ? null :
                        cb.like(cb.lower((root.get("nome"))), "%" + nome.toLowerCase() + "%");
    }
    public static Specification<Exemplo> temNumero(Integer numero) {
        return (root, query, cb) ->
                numero == null ? null :
                        cb.equal(root.get("numero"), numero);
    }
    public static Specification<Exemplo> temExemploEnum(String exemploEnumStr) {
        return (root, query, cb) -> {
            if (exemploEnumStr == null || exemploEnumStr.isBlank()) {
                return null;
            }
            try {
                ExemploEnum valorEnum = ExemploEnum.valueOf(exemploEnumStr);
                return cb.equal(root.get("exemploEnum"), valorEnum);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Exemplo> ativo() {
        return (root, query, cb) ->
                cb.equal(root.get("ativo"), true);
    }
}
