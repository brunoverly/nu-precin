package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    @Query(
            "SELECT e FROM Produto e WHERE e.id = :id AND e.ativo = true"
    )
    Optional<Produto> findByIdAtivo(Long id);

    Page<Produto> findAll(Specification<Produto> specification, Pageable pageable);
}
