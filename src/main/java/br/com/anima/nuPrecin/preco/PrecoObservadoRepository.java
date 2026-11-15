package br.com.anima.nuPrecin.preco;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;

public interface PrecoObservadoRepository extends Repository<PrecoObservado, Long> {
    PrecoObservado save(PrecoObservado preco);

    Page<PrecoObservado> findByIdProdutoOrderByObservadoEmDescIdDesc(Long idProduto, Pageable pageable);

    Page<PrecoObservado> findByIdProdutoAndIdEstabelecimentoOrderByObservadoEmDescIdDesc(
            Long idProduto, Long idEstabelecimento, Pageable pageable);

    String FILTROS_HISTORICO = """
            FROM precos_observados p
            WHERE p.id_produto = COALESCE(:idProduto, p.id_produto)
              AND p.id_estabelecimento = COALESCE(:idEstabelecimento, p.id_estabelecimento)
              AND p.id_usuario = COALESCE(:idUsuario, p.id_usuario)
              AND p.origem = COALESCE(:origem, p.origem)
            """;

    @Query(value = "SELECT p.* " + FILTROS_HISTORICO + " ORDER BY p.observado_em DESC, p.id DESC",
            countQuery = "SELECT COUNT(*) " + FILTROS_HISTORICO, nativeQuery = true)
    Page<PrecoObservado> listarObservacoes(Long idProduto, Long idEstabelecimento, Long idUsuario,
                                          String origem, Pageable pageable);

    String FILTROS_ATUAIS = """
            FROM precos_observados p
            JOIN produtos produto ON produto.id = p.id_produto AND produto.ativo = TRUE
            JOIN estabelecimentos loja ON loja.id = p.id_estabelecimento AND loja.ativo = TRUE
            WHERE p.origem = 'MANUAL'
              AND p.observado_em >= :limite AND p.observado_em <= :agora
              AND p.id_produto = COALESCE(:idProduto, p.id_produto)
              AND p.id_estabelecimento = COALESCE(:idEstabelecimento, p.id_estabelecimento)
              AND NOT EXISTS (
                  SELECT 1 FROM precos_observados outra
                  WHERE outra.id_produto = p.id_produto
                    AND outra.id_estabelecimento = p.id_estabelecimento
                    AND outra.origem = 'MANUAL' AND outra.observado_em <= :agora
                    AND (outra.observado_em > p.observado_em
                        OR (outra.observado_em = p.observado_em AND outra.id > p.id))
              )
              AND p.id_usuario = COALESCE(:idUsuario, p.id_usuario)
            """;

    @Query(value = "SELECT p.* " + FILTROS_ATUAIS + " ORDER BY p.observado_em DESC, p.id DESC",
            countQuery = "SELECT COUNT(*) " + FILTROS_ATUAIS, nativeQuery = true)
    Page<PrecoObservado> listarAtuais(Long idProduto, Long idEstabelecimento, Long idUsuario,
                                     Instant limite, Instant agora, Pageable pageable);
}
