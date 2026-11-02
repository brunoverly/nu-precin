package br.com.anima.nuPrecin.endereco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    @Query("""
            select en.id from Endereco en
            where (en.latitude is null or en.longitude is null)
              and (en.geocodificacaoTentadaEm is null or en.geocodificacaoTentadaEm < :antesDe)
              and exists (select e.id from Estabelecimento e where e.endereco = en and e.ativo = true)
            order by case when en.geocodificacaoTentadaEm is null then 0 else 1 end,
                     en.geocodificacaoTentadaEm, en.id
            """)
    List<Long> buscarPendentes(@Param("antesDe") LocalDateTime antesDe, Pageable pageable);
}
