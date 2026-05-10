package br.com.anima.nuPrecin.auth.dto;

public record LoginResponseDto(
        String nome,
        String email,
        String token) {
}