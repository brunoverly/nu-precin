package br.com.anima.nuPrecin.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Collection;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    @Query(
            "SELECT e FROM Produto e WHERE e.id = :id AND e.ativo = true"
    )
    Optional<Produto> findByIdAtivo(Long id);
    Optional<Produto> findByCodigoDeBarrasInAndAtivoTrue(Collection<String> codigos);
    Optional<Produto> findByQrCodeAndAtivoTrue(String qrCode);

    Page<Produto> findAll(Specification<Produto> specification, Pageable pageable);
}
