package br.dev.mhc.security.services.impl;

import br.dev.mhc.security.context.AuthContext;
import br.dev.mhc.security.dtos.CredentialsDTO;
import br.dev.mhc.security.dtos.RefreshTokenRequestDTO;
import br.dev.mhc.security.dtos.TokenResponseDTO;
import br.dev.mhc.security.dtos.UserDTO;
import br.dev.mhc.security.exceptions.InvalidTokenException;
import br.dev.mhc.security.services.AuthService;
import br.dev.mhc.security.services.RevokedTokenService;
import br.dev.mhc.security.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static java.util.Objects.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenService tokenService;
    private final RevokedTokenService revokedTokenService;
    private final UserDetailsService userDetailsService;
    private final ApplicationContext context;

    @Override
    public Authentication authenticate(CredentialsDTO credentials) {
        requireNonNull(credentials);
        requireNonNull(credentials.username());
        requireNonNull(credentials.password());

        AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.username(), credentials.password(), Collections.emptyList())
        );
    }

    @Override
    public Authentication authenticateWithToken(String token) {
        if (!tokenService.isValidToken(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }
        String username = tokenService.extractUsername(token);
        UUID tokenUuid = tokenService.extractTokenUuid(token);
        AuthContext.setTokenUuid(tokenUuid);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        try {
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } finally {
            AuthContext.clear();
        }
    }

    @Override
    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        requireNonNull(refreshTokenRequest);
        requireNonNull(refreshTokenRequest.refreshToken());

        String refreshToken = refreshTokenRequest.refreshToken();

        boolean isValidToken = tokenService.isValidToken(refreshToken);
        if (!isValidToken) {
            throw new InvalidTokenException("Invalid refresh token.");
        }
        String username = tokenService.extractUsername(refreshToken);
        UUID tokenUuid = tokenService.extractTokenUuid(refreshToken);
        AuthContext.setTokenUuid(tokenUuid);
        try {
            var userDetails = userDetailsService.loadUserByUsername(username);
            return generateTokenResponse(userDetails);
        } finally {
            AuthContext.clear();
        }
    }

    @Override
    public TokenResponseDTO generateTokenResponse(UserDetails userDetails) {
        var accessToken = tokenService.generateAccessToken(userDetails);
        var refreshToken = tokenService.generateRefreshToken(userDetails);
        var claims = tokenService.parseClaims(accessToken);
        var expiration = claims.getExpiration();
        var iat = (Long) claims.get("iat");
        var expiresIn = expiration.getTime() / 1000 - iat;

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .createdAt(Instant.now().toEpochMilli())
                .expiresIn(expiresIn)
                .build();
    }

    @Override
    public TokenResponseDTO generateTokenResponse(Authentication authentication) {
        return generateTokenResponse((UserDetailsImpl) authentication.getPrincipal());
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
        var userDetails = getCurrentUserDetails();
        if (isNull(userDetails) || isNull(userDetails.getTokenUuid())) {
            return;
        }
        revokedTokenService.revokeToken(userDetails.getTokenUuid(), userDetails.getUserId());
    }

    @Override
    public UserDTO getCurrentAuthenticatedUser() {
        var userDetails = getCurrentUserDetails();
        return nonNull(userDetails) ? new UserDTO(userDetails.getUser()) : null;
    }

    private UserDetailsImpl getCurrentUserDetails() {
        try {
            return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

}
