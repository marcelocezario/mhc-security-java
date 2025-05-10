package br.dev.mhc.security.services;

import br.dev.mhc.security.enums.TokenUsageType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;

public interface TokenService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isValidToken(String token);

    Claims parseClaims(String token);

    String extractUsername(String token);

    Date extractExpiration(String token);

    UUID extractTokenUuid(String token);

    TokenUsageType extractTokenUsageType(String token);

}
