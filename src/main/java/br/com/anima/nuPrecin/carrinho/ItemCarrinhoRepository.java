package br.com.anima.nuPrecin.carrinho;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    Optional<ItemCarrinho> findByIdAndAtivoTrue(Long id);
    List<ItemCarrinho> findByCarrinhoIdAndAtivoTrue(Long idCarrinho);
}
