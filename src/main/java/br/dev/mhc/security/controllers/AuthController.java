package br.dev.mhc.security.controllers;

import br.dev.mhc.security.constants.RouteConstants;
import br.dev.mhc.security.dtos.CredentialsDTO;
import br.dev.mhc.security.dtos.RefreshTokenRequestDTO;
import br.dev.mhc.security.dtos.TokenResponseDTO;
import br.dev.mhc.security.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.isNull;

@RestController
@RequestMapping(value = RouteConstants.AUTH_ROUTE)
public record AuthController(
        AuthService authService
) {

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody CredentialsDTO credentialsDTO) {
        var authenticate = authService.authenticate(credentialsDTO);
        var tokenResponse = authService.generateTokenResponse(authenticate);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid Authorization header");
        }

        String refreshToken = authorizationHeader.replace("Bearer ", "").trim();

        var refreshTokenRequest = new RefreshTokenRequestDTO(refreshToken);

        var token = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

}
