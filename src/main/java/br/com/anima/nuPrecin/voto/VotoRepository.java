package br.com.anima.nuPrecin.voto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    interface VotoPromocaoRankingProjection {
        Long getIdPromocao();
        Long getTotalVotos();
    }

    Optional<Voto> findByIdAndAtivoTrue(Long id);
    List<Voto> findByPromocaoIdAndAtivoTrue(Long idPromocao);
    List<Voto> findByUsuarioIdAndAtivoTrue(Long idUsuario);
    Optional<Voto> findByUsuarioIdAndPromocaoIdAndAtivoTrue(Long idUsuario, Long idPromocao);
    List<Voto> findByAtivoTrueAndVotoAndDataVotoBetweenOrderByDataVotoDesc(VotoEnum voto, LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("""
            SELECT v.promocao.id AS idPromocao, COUNT(v.id) AS totalVotos
            FROM Voto v
            WHERE v.ativo = true
              AND v.voto = :voto
              AND v.dataVoto BETWEEN :dataInicio AND :dataFim
            GROUP BY v.promocao.id
            ORDER BY COUNT(v.id) DESC
            """)
    List<VotoPromocaoRankingProjection> buscarRankingPromocoesDesc(@Param("voto") VotoEnum voto,
                                                                   @Param("dataInicio") LocalDateTime dataInicio,
                                                                   @Param("dataFim") LocalDateTime dataFim);

    @Query("""
            SELECT v.promocao.id AS idPromocao, COUNT(v.id) AS totalVotos
            FROM Voto v
            WHERE v.ativo = true
              AND v.voto = :voto
              AND v.dataVoto BETWEEN :dataInicio AND :dataFim
            GROUP BY v.promocao.id
            ORDER BY COUNT(v.id) ASC
            """)
    List<VotoPromocaoRankingProjection> buscarRankingPromocoesAsc(@Param("voto") VotoEnum voto,
                                                                  @Param("dataInicio") LocalDateTime dataInicio,
                                                                  @Param("dataFim") LocalDateTime dataFim);
}
