package br.com.anima.nuPrecin.estabelecimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long>, JpaSpecificationExecutor<Estabelecimento> {
    Optional<Estabelecimento> findByIdAndAtivoTrue(Long id);
    Page<Estabelecimento> findAll(Specification<Estabelecimento> specification, Pageable pageable);

    @Query(value = """
            SELECT e.* FROM estabelecimentos e
            JOIN (
                SELECT en.id, 6371.0088 * 2 * ASIN(SQRT(LEAST(1.0,
                    POWER(SIN(RADIANS(en.latitude - :latitude) / 2), 2)
                    + COS(RADIANS(:latitude)) * COS(RADIANS(en.latitude))
                    * POWER(SIN(RADIANS(en.longitude - :longitude) / 2), 2)
                ))) AS distancia_km
                FROM enderecos en
                WHERE en.latitude IS NOT NULL AND en.longitude IS NOT NULL
            ) localizacao ON localizacao.id = e.id_endereco
            WHERE e.ativo = true AND localizacao.distancia_km <= :raioKm
            ORDER BY localizacao.distancia_km, e.id
            """, nativeQuery = true)
    List<Estabelecimento> buscarProximos(@Param("latitude") double latitude,
                                       @Param("longitude") double longitude,
                                       @Param("raioKm") double raioKm);
}
