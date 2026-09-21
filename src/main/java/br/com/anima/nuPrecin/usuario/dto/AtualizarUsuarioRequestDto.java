package br.com.anima.nuPrecin.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AtualizarUsuarioRequest", description = "Atualização de perfil com troca de senha protegida pela senha atual")
public record AtualizarUsuarioRequestDto(
        @Schema(example = "Administrador NuPrecin")
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @Schema(example = "admin@nuprecin.com.br")
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        @Schema(description = "URL opcional da foto; se omitida, a foto atual é preservada", example = "https://cdn.example.com/admin.jpg", nullable = true)
        String foto,
        @Schema(example = "123", format = "password")
        @NotBlank(message = "senhaAtual é obrigatória")
        String senhaAtual,
        @Schema(example = "654321", format = "password")
        @NotBlank(message = "novaSenha é obrigatória")
        String novaSenha
) {
}
