package br.com.anima.nuPrecin.estabelecimento.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import br.com.anima.nuPrecin.endereco.dto.EnderecoRequestDto;

@Schema(name = "EstabelecimentoRequest", description = "Dados de estabelecimento. Use idEndereco ou endereco, nunca os dois.")
public record EstabelecimentoRequestDto(
        @Schema(description = "MERCADO, SUPERMERCADO ou ATACADAO", example = "SUPERMERCADO")
        @NotBlank(message = "tipo é obrigatório")
        String tipo,
        @Schema(example = "Supermercado BH")
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @Schema(description = "URL opcional da foto", example = "https://cdn.example.com/mercado.jpg", nullable = true)
        String foto,
        @Schema(example = "3132224400")
        @NotBlank(message = "telefone é obrigatório")
        String telefone,
        @Schema(description = "ID de endereço existente; não envie junto com endereco", example = "1", nullable = true)
        Long idEndereco,
        @Valid
        @Schema(description = "Novo endereço; não envie junto com idEndereco", nullable = true)
        EnderecoRequestDto endereco,
        @Schema(description = "Usuário criador/proprietário", example = "1")
        @NotNull(message = "idUsuario é obrigatório")
        Long idUsuario
) {
}
