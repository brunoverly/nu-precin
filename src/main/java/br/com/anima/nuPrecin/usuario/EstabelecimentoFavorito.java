package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_estabelecimentos_favoritos",
        uniqueConstraints = @UniqueConstraint(name = "uq_usuario_estabelecimento_favorito", columnNames = {"id_usuario", "id_estabelecimento"}))
@Getter
@NoArgsConstructor
public class EstabelecimentoFavorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estabelecimento", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
