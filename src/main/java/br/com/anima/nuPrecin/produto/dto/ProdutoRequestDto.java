package br.com.anima.nuPrecin.produto.dto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;
import br.com.anima.nuPrecin.produto.UnidadeEmbalagem;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProdutoRequest", description = "Dados de catálogo. A imagem pode ser URL ou ser enviada por multipart depois.")
public record ProdutoRequestDto(
                @Schema(example = "Coca-Cola 350ml")
                @NotBlank(message = "nome é obrigatório")
                @Size(max = 250)
                String nome,
                @Schema(example = "Refrigerante sabor cola em lata de 350ml")
                @NotBlank(message = "descrição é obrigatória")
                @Size(max = 500)
                String descricao,
                @Schema(example = "Coca-Cola")
                @NotBlank(message = "marca é obrigatória")
                @Size(max = 250)
                String marca,
                @Schema(example = "7894900011517")
                @NotBlank(message = "código de barras é obrigatório")
                @Size(max = 100)
                String codigoDeBarras,
                @Schema(description = "URL opcional da imagem", example = "https://project.supabase.co/storage/v1/object/public/bucket/produtos/1/imagens/image.png", nullable = true)
                @Size(max = 500)
                String imagem,
                @Schema(example = "REFRIGERANTES")
                @NotNull(message = "categoria é obrigatória")
                ProdutoEnum categoria,
                @Schema(description = "Usuário criador/proprietário", example = "1")
                @NotNull(message = "idUsuario é obrigatório")
                Long idUsuario,
                @Schema(description = "Conteúdo opcional de um QR próprio do produto. No PUT, omitir preserva o valor e enviar vazio remove.")
                @Size(max = 250)
                String qrCode,
                @Schema(description = "Conteúdo total da embalagem. Opcional em conjunto com unidadeEmbalagem.", example = "2000")
                @Positive
                @Digits(integer = 12, fraction = 3)
                BigDecimal quantidadeEmbalagem,
                @Schema(description = "Unidade da embalagem: gramas (G), mililitros (ML) ou unidades (UN).", example = "ML")
                UnidadeEmbalagem unidadeEmbalagem) {
}
