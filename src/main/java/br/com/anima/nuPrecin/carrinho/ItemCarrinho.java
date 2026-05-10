package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.produto.Produto;
import jakarta.persistence.*;
import lombok.*;

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

    private Integer quantidade;
    private boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrinho")
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @PrePersist
    public void onCreate() {
        this.ativo = true;
    }
}
