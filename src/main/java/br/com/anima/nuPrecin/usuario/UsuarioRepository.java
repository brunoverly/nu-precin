package br.com.anima.nuPrecin.usuario;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByIdAndAtivoTrue(Long id);
    Page<Usuario> findAll(Specification<Usuario> specification, Pageable pageable);
    Optional<Usuario> findByEmailIgnoreCaseAndAtivoTrue(String email);
    Optional<Usuario> findByGoogleSub(String googleSub);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where lower(u.email) = :email and u.ativo = true")
    Optional<Usuario> buscarPorEmailParaVinculo(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.id = :id and u.ativo = true")
    Optional<Usuario> buscarAtivoParaAlteracao(Long id);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
