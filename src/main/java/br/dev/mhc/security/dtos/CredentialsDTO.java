package br.dev.mhc.security.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@Builder
public class CredentialsDTO implements Serializable {

    private final String username;
    private final String password;
}
