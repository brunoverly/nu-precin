package br.com.anima.nuPrecin.endereco;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enderecos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    @Column(length = 8)
    private String cep;
    private Double latitude;
    private Double longitude;
    private LocalDateTime geocodificacaoTentadaEm;
    @Version
    private Long versao;
}
