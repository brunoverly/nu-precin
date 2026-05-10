package br.com.anima.nuPrecin.usuario;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByIdAndAtivoTrue(Long id);
    Page<Usuario> findAll(Specification<Usuario> specification, Pageable pageable);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
