package br.com.anima.nuPrecin.promocao;


import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.produto.Produto;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.voto.Voto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promocoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Promocao {
    private static final ZoneId FUSO_VIGENCIA = ZoneId.of("America/Sao_Paulo");

    public static LocalDateTime agoraNaVigencia() {
        return LocalDateTime.now(FUSO_VIGENCIA);
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal precoOriginal;
    private BigDecimal precoPromocao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean ativo;
    private boolean enquantoDurarEstoque;
    private LocalDateTime ultimaConfirmacaoEm;

    public static LocalDateTime limiteConfirmacao(LocalDateTime agora) {
        return agora.minusHours(24);
    }

    public boolean estaVigente(LocalDateTime agora) {
        return (dataInicio == null || !agora.isBefore(dataInicio))
                && (dataFim != null ? !agora.isAfter(dataFim)
                    : enquantoDurarEstoque && ultimaConfirmacaoEm != null
                        && ultimaConfirmacaoEm.isAfter(limiteConfirmacao(agora)));
    }

    @Column(name = "id_produto")
    private Long idProduto;
    @Column(name = "id_estabelecimento")
    private Long idEstabelecimento;
    @Column(name = "id_usuario")
    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", insertable = false, updatable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estabelecimento", insertable = false, updatable = false)
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "promocao")
    @Builder.Default
    private List<Voto> votos = new ArrayList<>();

    @PrePersist
    public void onCreate(){
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }
}
