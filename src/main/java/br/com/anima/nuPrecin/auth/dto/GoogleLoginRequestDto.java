package br.com.anima.nuPrecin.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoogleLoginRequestDto(@NotBlank @Size(max = 8192) String idToken) {}
