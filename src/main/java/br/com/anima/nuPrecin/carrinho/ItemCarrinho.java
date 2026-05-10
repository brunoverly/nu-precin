package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.promocao.Promocao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_carrinho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ItemCarrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidadeItem;
    private BigDecimal precoItem;
    private BigDecimal precoTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_promocao")
    private Promocao promocao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrinho")
    private Carrinho carrinho;

    @PrePersist
    @PreUpdate
    public void calcularPrecoTotal() {
        if (precoItem != null && quantidadeItem != null) {
            this.precoTotal = precoItem.multiply(java.math.BigDecimal.valueOf(quantidadeItem));
        } else {
            this.precoTotal = BigDecimal.ZERO;
        }
    }
}

