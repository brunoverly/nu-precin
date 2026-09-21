package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ResetarSenhaRequest", description = "Código de recuperação e nova senha")
public record ResetarSenhaRequestDto(
        @Schema(example = "admin@nuprecin.com.br")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        @Schema(description = "Código numérico recebido por e-mail", example = "4821", minLength = 4, maxLength = 4)
        @NotBlank(message = "codigo é obrigatório")
        @Pattern(regexp = "\\d{4}", message = "codigo deve possuir 4 dígitos")
        String codigo,
        @Schema(example = "654321", format = "password")
        @NotBlank(message = "novaSenha é obrigatória")
        String novaSenha
) {
}
