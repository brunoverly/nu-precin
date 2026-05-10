package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VotoEnum voto;

    private LocalDateTime dataVoto;
    private boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_promocao")
    private Promocao promocao;

    @PrePersist
    public void onCreate() {
        this.dataVoto = LocalDateTime.now();
        this.ativo = true;
    }
}
