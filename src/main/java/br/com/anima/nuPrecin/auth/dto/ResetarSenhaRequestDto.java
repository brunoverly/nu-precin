package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetarSenhaRequestDto(
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        @NotBlank(message = "codigo é obrigatório")
        @Pattern(regexp = "\\d{4}", message = "codigo deve possuir 4 dígitos")
        String codigo,
        @NotBlank(message = "novaSenha é obrigatória")
        String novaSenha
) {
}
