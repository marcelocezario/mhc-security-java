package br.dev.mhc.security.dtos;

import java.io.Serializable;

public record CredentialsDTO(String username, String password) implements Serializable {
}
