package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.produto.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_produtos_favoritos",
        uniqueConstraints = @UniqueConstraint(name = "uq_usuario_produto_favorito", columnNames = {"id_usuario", "id_produto"}))
@Getter
@NoArgsConstructor
public class ProdutoFavorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
