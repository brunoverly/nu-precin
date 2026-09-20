package br.com.anima.nuPrecin.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cadastros_pendentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CadastroPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 250, nullable = false)
    private String nome;

    @Column(name = "email", length = 250, nullable = false)
    private String email;

    @Column(name = "foto", length = 500)
    private String foto;

    @Column(name = "senha_hash", length = 250, nullable = false)
    private String senhaHash;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "consumido_em")
    private LocalDateTime consumidoEm;

    @PrePersist
    public void onCreate() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
