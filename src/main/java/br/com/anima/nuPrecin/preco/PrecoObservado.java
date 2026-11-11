package br.com.anima.nuPrecin.preco;

import br.com.anima.nuPrecin.promocao.Promocao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "precos_observados")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PrecoObservado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_produto", nullable = false)
    private Long idProduto;

    @Column(name = "id_estabelecimento", nullable = false)
    private Long idEstabelecimento;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private Instant observadoEm;

    @Column(nullable = false)
    private Instant registradoEm;

    @Column(nullable = false, length = 30)
    private String origem;

    @Column(name = "id_promocao")
    private Long idPromocao;

    @Column(precision = 12, scale = 2)
    private BigDecimal precoOriginal;

    private LocalDateTime inicioVigencia;
    private LocalDateTime fimVigencia;
    private boolean enquantoDurarEstoque;

    PrecoObservado(Long idProduto, Long idEstabelecimento, Long idUsuario, BigDecimal valor,
                   Instant observadoEm, Instant registradoEm) {
        this.idProduto = idProduto;
        this.idEstabelecimento = idEstabelecimento;
        this.idUsuario = idUsuario;
        this.valor = valor;
        this.observadoEm = observadoEm;
        this.registradoEm = registradoEm;
        this.origem = "MANUAL";
    }

    PrecoObservado(Promocao promocao, Long idUsuario, Instant agora) {
        this(promocao.getIdProduto(), promocao.getIdEstabelecimento(), idUsuario,
                promocao.getPrecoPromocao(), agora, agora);
        this.origem = "PROMOCAO";
        this.idPromocao = promocao.getId();
        this.precoOriginal = promocao.getPrecoOriginal();
        this.inicioVigencia = promocao.getDataInicio();
        this.fimVigencia = promocao.getDataFim();
        this.enquantoDurarEstoque = promocao.isEnquantoDurarEstoque();
    }
}
