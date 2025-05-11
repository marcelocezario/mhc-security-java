package br.dev.mhc.security.services;

import br.dev.mhc.security.dtos.CredentialsDTO;
import br.dev.mhc.security.dtos.RefreshTokenRequestDTO;
import br.dev.mhc.security.dtos.TokenResponseDTO;
import br.dev.mhc.security.dtos.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    Authentication authenticate(CredentialsDTO credentials);

    Authentication authenticateWithToken(String token);

    TokenResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest);

    TokenResponseDTO generateTokenResponse(UserDetails userDetails);

    TokenResponseDTO generateTokenResponse(Authentication authentication);

    boolean hasCurrentUserRole(String role);

    void logout();

    UserDTO getCurrentAuthenticatedUser();

}
