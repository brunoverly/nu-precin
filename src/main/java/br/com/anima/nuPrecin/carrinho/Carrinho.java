package br.com.anima.nuPrecin.carrinho;

import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String nome;
    private LocalDateTime dataCadastro;
    private boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemCarrinho> itens = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }
}
