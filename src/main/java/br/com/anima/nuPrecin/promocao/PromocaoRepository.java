package br.com.anima.nuPrecin.promocao;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.anima.nuPrecin.produto.dto.ComparativoPrecoResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromocaoRepository extends JpaRepository<Promocao, Long>, JpaSpecificationExecutor<Promocao> {
    Optional<Promocao> findByIdAndAtivoTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Promocao p where p.id = :id and p.ativo = true")
    Optional<Promocao> buscarAtivaParaAlteracao(Long id);

    @Query("""
            select new br.com.anima.nuPrecin.produto.dto.ComparativoPrecoResponseDto(
                e.id, e.nome, p.id, p.precoOriginal, p.precoPromocao, p.dataInicio, p.dataFim,
                p.enquantoDurarEstoque, p.ultimaConfirmacaoEm)
            from Promocao p join p.estabelecimento e
            where p.idProduto = :idProduto and p.ativo = true and e.ativo = true
                and p.dataInicio <= :agora
                and (p.dataFim >= :agora or (p.dataFim is null and p.enquantoDurarEstoque = true
                    and p.ultimaConfirmacaoEm > :limiteConfirmacao))
                and not exists (
                    select 1 from Promocao outra
                    where outra.idProduto = p.idProduto and outra.idEstabelecimento = e.id
                        and outra.ativo = true and outra.dataInicio <= :agora
                        and (outra.dataFim >= :agora or (outra.dataFim is null and outra.enquantoDurarEstoque = true
                            and outra.ultimaConfirmacaoEm > :limiteConfirmacao))
                        and (outra.precoPromocao < p.precoPromocao
                            or (outra.precoPromocao = p.precoPromocao and outra.id < p.id))
                )
            order by p.precoPromocao, e.id
            """)
    List<ComparativoPrecoResponseDto> compararPrecosVigentes(Long idProduto, LocalDateTime agora, LocalDateTime limiteConfirmacao);
}
