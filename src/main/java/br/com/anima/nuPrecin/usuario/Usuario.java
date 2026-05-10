package br.com.anima.nuPrecin.usuario;

import br.com.anima.nuPrecin.carrinho.Carrinho;
import br.com.anima.nuPrecin.estabelecimento.Estabelecimento;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.voto.Voto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String foto;
    private String senha;
    private LocalDateTime dataCadastro;
    private boolean ativo;

    @OneToMany(mappedBy = "usuario")
    @Builder.Default
    private List<Promocao> promocoes = new ArrayList<>();

    @OneToOne(mappedBy = "usuario")
    private Carrinho carrinho;

    @OneToMany(mappedBy = "usuario")
    @Builder.Default
    private List<Estabelecimento> estabelecimentos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    @Builder.Default
    private List<Voto> votos = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }
}
