package br.com.anima.nuPrecin.promocao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PromocaoRepository extends JpaRepository<Promocao, Long>, JpaSpecificationExecutor<Promocao> {
    Optional<Promocao> findByIdAndAtivoTrue(Long id);
}
