package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.carrinho.ItemCarrinho;
import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "produto")
    @Builder.Default
    private List<ItemCarrinho> itensCarrinho = new ArrayList<>();
 
  @PrePersist
    public void prePersist(){
    
        this.ativo = true;
    }
}
