package br.dev.mhc.security.services;

import br.dev.mhc.security.dtos.CredentialsDTO;
import br.dev.mhc.security.dtos.RefreshTokenRequestDTO;
import br.dev.mhc.security.dtos.TokenResponseDTO;
import br.dev.mhc.security.dtos.UserDTO;

public interface AuthService {

    TokenResponseDTO authenticate(CredentialsDTO credentials);

    TokenResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest);

    boolean hasCurrentUserRole(String role);

    void logout();

    UserDTO getCurrentAuthenticatedUser();

}
