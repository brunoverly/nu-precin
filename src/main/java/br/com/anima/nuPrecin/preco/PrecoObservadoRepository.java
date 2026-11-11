package br.com.anima.nuPrecin.preco;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface PrecoObservadoRepository extends Repository<PrecoObservado, Long> {
    PrecoObservado save(PrecoObservado preco);

    Page<PrecoObservado> findByIdProdutoOrderByObservadoEmDescIdDesc(Long idProduto, Pageable pageable);

    Page<PrecoObservado> findByIdProdutoAndIdEstabelecimentoOrderByObservadoEmDescIdDesc(
            Long idProduto, Long idEstabelecimento, Pageable pageable);
}
