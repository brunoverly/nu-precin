package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.produto.ProdutoEnum;
import org.springframework.data.jpa.domain.Specification;

public class ProdutoSpecification {
    public static Specification<Produto> temNome(String nome) {
        return (root, query, cb) ->
                nome == null|| nome.isBlank() ? null :
                        cb.like(cb.lower((root.get("nome"))), "%" + nome.toLowerCase() + "%");
    }
    public static Specification<Produto> temMarca(String marca) {
        return (root, query, cb) ->
                marca == null ? null :
                        cb.equal(root.get("marca"), marca);
    }
    public static Specification<Produto> temCategoria(String produtoEnumStr) {
        return (root, query, cb) -> {
            if (produtoEnumStr == null || produtoEnumStr.isBlank()) {
                return null;
            }
            try {
                ProdutoEnum valorEnum = ProdutoEnum.valueOf(produtoEnumStr);
                return cb.equal(root.get("produtoEnum"), valorEnum);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Produto> ativo() {
        return (root, query, cb) ->
                cb.equal(root.get("ativo"), true);
    }
}
