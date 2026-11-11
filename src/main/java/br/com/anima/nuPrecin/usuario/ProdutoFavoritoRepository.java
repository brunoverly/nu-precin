package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoFavoritoRepository extends JpaRepository<ProdutoFavorito, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO usuarios_produtos_favoritos (id_usuario, id_produto, criado_em)
            VALUES (:idUsuario, :idProduto, CURRENT_TIMESTAMP)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    void adicionarSeAusente(@Param("idUsuario") Long idUsuario, @Param("idProduto") Long idProduto);

    @Modifying
    @Query("delete from ProdutoFavorito f where f.usuario.id = :idUsuario and f.produto.id = :idProduto")
    void remover(@Param("idUsuario") Long idUsuario, @Param("idProduto") Long idProduto);

    @Query("""
            select e from ProdutoFavorito f
            join f.produto e
            join fetch e.usuario
            where f.usuario.id = :idUsuario and e.ativo = true
            order by e.nome, e.id
            """)
    List<Produto> listarAtivos(@Param("idUsuario") Long idUsuario);
}
