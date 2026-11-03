package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstabelecimentoFavoritoRepository extends JpaRepository<EstabelecimentoFavorito, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO usuarios_estabelecimentos_favoritos (id_usuario, id_estabelecimento, criado_em)
            VALUES (:idUsuario, :idEstabelecimento, CURRENT_TIMESTAMP)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    void adicionarSeAusente(@Param("idUsuario") Long idUsuario, @Param("idEstabelecimento") Long idEstabelecimento);

    @Modifying
    @Query("delete from EstabelecimentoFavorito f where f.usuario.id = :idUsuario and f.estabelecimento.id = :idEstabelecimento")
    void remover(@Param("idUsuario") Long idUsuario, @Param("idEstabelecimento") Long idEstabelecimento);

    @Query("""
            select e from EstabelecimentoFavorito f
            join f.estabelecimento e
            left join fetch e.endereco
            join fetch e.usuario
            where f.usuario.id = :idUsuario and e.ativo = true
            order by e.nome, e.id
            """)
    List<Estabelecimento> listarAtivos(@Param("idUsuario") Long idUsuario);
}
