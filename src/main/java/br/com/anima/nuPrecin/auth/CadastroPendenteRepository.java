package br.com.anima.nuPrecin.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CadastroPendenteRepository extends JpaRepository<CadastroPendente, Long> {

    Optional<CadastroPendente> findByEmailIgnoreCaseAndConsumidoEmIsNull(String email);
}
