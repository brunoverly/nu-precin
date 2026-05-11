package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrinhos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal precoTotal;
    private LocalDateTime dataCadastro;
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<ItemCarrinho> itens = new java.util.ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
        calcularPrecoTotal();
    }

    @PreUpdate
    public void onUpdate() {
        calcularPrecoTotal();
    }

    private void calcularPrecoTotal() {
        if (itens == null || itens.isEmpty()) {
            this.precoTotal = BigDecimal.ZERO;
            return;
        }

        this.precoTotal = itens.stream()
                .map(item -> item.getPrecoTotal() == null ? BigDecimal.ZERO : item.getPrecoTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
