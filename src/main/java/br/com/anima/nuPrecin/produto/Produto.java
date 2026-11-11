package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private String marca;
    private String codigoDeBarras;
    private String qrCode;
    private String imagem;
    @Enumerated(EnumType.STRING)
    private ProdutoEnum categoria;
    private boolean ativo;
    @Column(precision = 15, scale = 3)
    private BigDecimal quantidadeEmbalagem;
    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private UnidadeEmbalagem unidadeEmbalagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    public void prePersist(){
        this.ativo = true;
    }
}
