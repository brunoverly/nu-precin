package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;
import jakarta.persistence.*;
import lombok.*;

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
 
  @PrePersist
    public void prePersist(){
    
        this.ativo = true;
    }
}
