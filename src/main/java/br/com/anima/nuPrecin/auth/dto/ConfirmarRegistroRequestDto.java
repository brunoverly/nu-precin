package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ConfirmarRegistroRequest", description = "Código enviado para confirmação da conta")
public record ConfirmarRegistroRequestDto(
        @Schema(example = "maria@example.com")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        @Schema(description = "Código numérico de quatro dígitos", example = "4821", minLength = 4, maxLength = 4)
        @NotBlank(message = "codigo é obrigatório")
        @Pattern(regexp = "\\d{4}", message = "codigo deve possuir 4 dígitos")
        String codigo
) {
}
