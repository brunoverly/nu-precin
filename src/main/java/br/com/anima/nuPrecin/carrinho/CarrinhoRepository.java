package br.com.anima.nuPrecin.carrinho;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByIdAndAtivoTrue(Long id);
    Optional<Carrinho> findByUsuarioIdAndAtivoTrue(Long idUsuario);
    Optional<Carrinho> findByUsuarioId(Long idUsuario);
}
