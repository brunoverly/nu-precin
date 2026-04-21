package br.com.anima.nuPrecin.exemplo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "exemplos")
public class Exemplo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer numero;
    private LocalDateTime dataHorarioCriado;
    @Enumerated(EnumType.STRING)
    private ExemploEnum exemploEnum;
    private boolean ativo;


    @PrePersist
    public void prePersist(){
        this.dataHorarioCriado = LocalDateTime.now();
        this.ativo = true;
    }
}
