package br.dev.mhc.security.controllers;

import br.dev.mhc.security.dtos.CredentialsDTO;
import br.dev.mhc.security.dtos.RefreshTokenRequestDTO;
import br.dev.mhc.security.dtos.TokenResponseDTO;
import br.dev.mhc.security.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.isNull;

@RestController
@RequestMapping(value = "${auth.controller.path}")
public record AuthController(
        AuthService authService
) {

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody CredentialsDTO credentialsDTO) {
        throw new RuntimeException("Method not implemented, added only for swagger configuration, application must use default spring security call");
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid Authorization header");
        }

        String refreshToken = authorizationHeader.replace("Bearer ", "").trim();

        var refreshTokenRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        var token = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(token);
    }

}
