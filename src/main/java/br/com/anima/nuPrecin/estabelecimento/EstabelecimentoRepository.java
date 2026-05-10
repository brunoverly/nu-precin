package br.com.anima.nuPrecin.estabelecimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long>, JpaSpecificationExecutor<Estabelecimento> {
    Optional<Estabelecimento> findByIdAndAtivoTrue(Long id);
    Page<Estabelecimento> findAll(Specification<Estabelecimento> specification, Pageable pageable);
}
