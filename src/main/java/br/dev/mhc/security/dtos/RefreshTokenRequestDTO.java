package br.dev.mhc.security.dtos;

import java.io.Serializable;

public record RefreshTokenRequestDTO(String refreshToken) implements Serializable {
}
