package br.dev.mhc.security.services.impl;

import br.dev.mhc.security.dtos.CredentialsDTO;
import br.dev.mhc.security.dtos.RefreshTokenRequestDTO;
import br.dev.mhc.security.dtos.TokenResponseDTO;
import br.dev.mhc.security.dtos.UserDTO;
import br.dev.mhc.security.enums.TokenUsageType;
import br.dev.mhc.security.exceptions.InvalidTokenException;
import br.dev.mhc.security.services.AuthService;
import br.dev.mhc.security.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponseDTO authenticate(CredentialsDTO credentials) {
        requireNonNull(credentials);
        requireNonNull(credentials.getUsername());
        requireNonNull(credentials.getPassword());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(), Collections.emptyList())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var tokenUUID = UUID.randomUUID();
        userDetails.setTokenUuid(tokenUUID);

        return generateTokeResponse(userDetails);
    }

    @Override
    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        requireNonNull(refreshTokenRequest);
        requireNonNull(refreshTokenRequest.getRefreshToken());

        String refreshToken = refreshTokenRequest.getRefreshToken();

        boolean isValidToken = tokenService.isValidToken(refreshToken, TokenUsageType.REFRESH_TOKEN);
        if (!isValidToken) {
            throw new InvalidTokenException("Invalid refresh token.");
        }
        String username = tokenService.extractUsername(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);

        UUID tokenUuid = tokenService.extractTokenUuid(refreshToken);
        ((UserDetailsImpl) userDetails).setTokenUuid(tokenUuid);

        return generateTokeResponse(userDetails);
    }

    @Override
    public boolean hasCurrentUserRole(String role) {
        if (isNull(role)) {
            return false;
        }
        UserDTO user = getCurrentAuthenticatedUser();
        if (isNull(user) || isNull(user.getRoles())) {
            return false;
        }
        return user.getRoles().stream().anyMatch(r -> r.equals(role));
    }

    @Override
    public void logout() {
        

    }

    @Override
    public UserDTO getCurrentAuthenticatedUser() {
        try {
            var userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new UserDTO(userDetails.getUser());
        } catch (Exception e) {
            return null;
        }
    }

    private TokenResponseDTO generateTokeResponse(UserDetails userDetails) {
        var accessToken = tokenService.generateAccessToken(userDetails);
        var refreshToken = tokenService.generateRefreshToken(userDetails);
        var createdAt = Instant.now().toEpochMilli();
        var expiration = tokenService.extractExpiration(accessToken);
        var expiresIn = expiration.getTime() - createdAt;

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .createdAt(Instant.now().toEpochMilli())
                .expiresIn(expiresIn)
                .build();
    }
}
