package br.com.anima.nuPrecin.produto.dto;

import br.com.anima.nuPrecin.produto.ProdutoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProdutoRequest", description = "Dados de catálogo. A imagem pode ser URL ou ser enviada por multipart depois.")
public record ProdutoRequestDto(
                @Schema(example = "Coca-Cola 350ml")
                @NotBlank(message = "nome é obrigatório")
                String nome,
                @Schema(example = "Refrigerante sabor cola em lata de 350ml")
                @NotBlank(message = "descrição é obrigatória")
                String descricao,
                @Schema(example = "Coca-Cola")
                @NotBlank(message = "marca é obrigatória")
                String marca,
                @Schema(example = "7894900011517")
                @NotBlank(message = "código de barras é obrigatório")
                String codigoDeBarras,
                @Schema(description = "URL opcional da imagem", example = "https://project.supabase.co/storage/v1/object/public/bucket/produtos/1/imagens/image.png", nullable = true)
                String imagem,
                @Schema(example = "BEBIDA")
                @NotNull(message = "categoria é obrigatória")
                ProdutoEnum categoria,
                @Schema(description = "Usuário criador/proprietário", example = "1")
                @NotNull(message = "idUsuario é obrigatório")
                Long idUsuario) {
}
