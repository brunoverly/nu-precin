package br.com.anima.nuPrecin.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DesafioVerificacaoRepository extends JpaRepository<DesafioVerificacao, Long> {

    @Modifying
    @Query("""
            UPDATE DesafioVerificacao d
               SET d.invalidadoEm = :agora
             WHERE d.tipo = :tipo
               AND d.cadastroPendente.id = :idCadastroPendente
               AND d.usadoEm IS NULL
               AND d.invalidadoEm IS NULL
            """)
    int invalidarDesafiosAtivosDoCadastro(@Param("tipo") DesafioTipo tipo,
                                          @Param("idCadastroPendente") Long idCadastroPendente,
                                          @Param("agora") LocalDateTime agora);

    @Query("""
            SELECT d
              FROM DesafioVerificacao d
             WHERE d.tipo = :tipo
               AND d.cadastroPendente.id = :idCadastroPendente
               AND d.usadoEm IS NULL
               AND d.invalidadoEm IS NULL
            """)
    Optional<DesafioVerificacao> buscarDesafioAtivoDoCadastro(
            @Param("tipo") DesafioTipo tipo,
            @Param("idCadastroPendente") Long idCadastroPendente);
}
