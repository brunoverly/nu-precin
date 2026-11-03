package br.com.anima.nuPrecin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "Dados do usuário autenticado e JWT")
public record LoginResponseDto(
        @Schema(description = "ID do usuário autenticado", example = "1")
        Long id,
        @Schema(example = "Administrador NuPrecin")
        String nome,
        @Schema(example = "admin@nuprecin.com.br")
        String email,
        @Schema(description = "JWT para o header Authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,
        @Schema(description = "Distância preferida para buscar estabelecimentos, em quilômetros", example = "5", allowableValues = {"1", "3", "5", "10"}, defaultValue = "5")
        int raioBuscaKm) {
}
