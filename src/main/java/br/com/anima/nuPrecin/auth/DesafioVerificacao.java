package br.com.anima.nuPrecin.auth;

import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "desafios_verificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DesafioVerificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50, nullable = false)
    private DesafioTipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cadastro_pendente")
    private CadastroPendente cadastroPendente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "email_destino", length = 250, nullable = false)
    private String emailDestino;

    @Column(name = "codigo_hash", length = 128, nullable = false)
    private String codigoHash;

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas;

    @Column(name = "max_tentativas", nullable = false)
    private Integer maxTentativas;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "usado_em")
    private LocalDateTime usadoEm;

    @Column(name = "invalidado_em")
    private LocalDateTime invalidadoEm;

    @PrePersist
    public void onCreate() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
        if (tentativas == null) {
            tentativas = 0;
        }
        if (maxTentativas == null) {
            maxTentativas = 5;
        }
    }
}
