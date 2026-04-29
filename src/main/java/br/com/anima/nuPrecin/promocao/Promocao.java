package br.com.anima.nuPrecin.promocao;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promocoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Promocao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal precoOriginal;
    private BigDecimal precoPromocao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean ativo;
    private Long idProduto;
    private Long idEstabelecimento;
    private Long idUsuario;

    @PrePersist
    public void onCreate(){
        this.dataCriacao = LocalDateTime.now();
        this.ativo = true;
    }
}
