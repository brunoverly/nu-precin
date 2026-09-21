package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReenviarConfirmacaoRequest")
public record ReenviarConfirmacaoRequestDto(
        @Schema(example = "maria@example.com")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email
) {
}
