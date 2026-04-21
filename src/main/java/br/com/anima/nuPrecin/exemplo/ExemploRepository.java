package br.com.anima.nuPrecin.exemplo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ExemploRepository extends JpaRepository<Exemplo, Long>, JpaSpecificationExecutor<Exemplo> {

    @Query(
            "SELECT e FROM Exemplo e WHERE e.id = :id AND e.ativo = true"
    )
    Optional<Exemplo> findByIdAtivo(Long id);

    Page<Exemplo> findAll(Specification<Exemplo> specification, Pageable pageable);
}
