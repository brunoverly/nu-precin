package br.com.anima.nuPrecin.estabelecimento;

import br.com.anima.nuPrecin.endereco.Endereco;
import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estabelecimentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Estabelecimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @Enumerated(EnumType.STRING)
    private EstabelecimentoTipo tipo;
    private String foto;
    private String telefone;
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "estabelecimento")
    @Builder.Default
    private List<Promocao> promocoes = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.ativo = true;
    }
}
