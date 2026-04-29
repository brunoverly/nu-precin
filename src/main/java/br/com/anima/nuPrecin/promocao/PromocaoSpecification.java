package br.com.anima.nuPrecin.promocao;

import org.springframework.data.jpa.domain.Specification;

public class PromocaoSpecification {
    public static Specification<Promocao> ativo() {
        return (root, query, cb) ->
                cb.equal(root.get("ativo"), true);
    }
    public static Specification<Promocao> temProduto(Long idProduto) {
        return (root, query, cb) ->
                idProduto == null ? null :
                        cb.equal(root.get("idProduto"), idProduto);
    }
    public static Specification<Promocao> temEstabelecimento(Long idEstabelecimento) {
        return (root, query, cb) ->
                idEstabelecimento == null ? null :
                        cb.equal(root.get("idEstabelecimento"), idEstabelecimento);
    }
    public static Specification<Promocao> temUsuario(Long idUsuario) {
        return (root, query, cb) ->
                idUsuario == null ? null :
                        cb.equal(root.get("idUsuario"), idUsuario);
    }
}
