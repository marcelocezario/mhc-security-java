package br.dev.mhc.security.services.impl;

import br.dev.mhc.security.config.TokenProperties;
import br.dev.mhc.security.enums.TokenUsageType;
import br.dev.mhc.security.services.RevokedTokenService;
import br.dev.mhc.security.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.*;

@Service
@RequiredArgsConstructor
class TokenServiceImpl implements TokenService {

    private final TokenProperties tokenProperties;
    private final RevokedTokenService revokedTokenService;

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        requireNonNull(userDetails);

        var userAuthenticated = (UserDetailsImpl) userDetails;

        Instant iat = Instant.now();

        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(userAuthenticated.getUsername())
                .expiration(Date.from(iat.plusMillis(tokenProperties.getAccessTokenExpiration())))
                .claim("iat", Date.from(iat))
                .claim("token_uuid", userAuthenticated.getTokenUuid().toString())
                .claim("token_usage", TokenUsageType.ACCESS_TOKEN.name())
                .claim("userId", userAuthenticated.getUserId())
                .claim("roles", userAuthenticated.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        jwtBuilder.signWith(getSecretKey());
        return jwtBuilder.compact();
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        requireNonNull(userDetails);

        var userAuthenticated = (UserDetailsImpl) userDetails;

        Instant iat = Instant.now();

        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(userAuthenticated.getUsername())
                .expiration(Date.from(iat.plusMillis(tokenProperties.getRefreshTokenExpiration())))
                .claim("iat", Date.from(iat))
                .claim("token_uuid", userAuthenticated.getTokenUuid().toString())
                .claim("token_usage", TokenUsageType.REFRESH_TOKEN.name());
        jwtBuilder.signWith(getSecretKey());
        return jwtBuilder.compact();
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            if (isNull(token)) {
                return false;
            }
            Claims claims = parseClaims(token);
            if (isNull(claims) || isNull(claims.getSubject())) {
                return false;
            }
            var tokenUuid = claims.get("token_uuid").toString();
            if (revokedTokenService.isTokenRevoked(UUID.fromString(tokenUuid))) {
                return false;
            }
            Date expirationDate = claims.getExpiration();
            Date now = Date.from(Instant.now());
            return now.before(expirationDate);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String extractUsername(String token) {
        var claims = parseClaims(token);
        return nonNull(claims) ? claims.getSubject() : null;
    }

    @Override
    public Date extractExpiration(String token) {
        var claims = parseClaims(token);
        return nonNull(claims) ? claims.getExpiration() : null;
    }

    @Override
    public UUID extractTokenUuid(String token) {
        var claims = parseClaims(token);
        if (isNull(claims)) {
            return null;
        }
        String uuidString = claims.get("token_uuid").toString();
        return UUID.fromString(uuidString);
    }

    @Override
    public TokenUsageType extractTokenUsageType(String token) {
        var claims = parseClaims(token);
        if (isNull(claims)) {
            return null;
        }
        Object tokenUsageClaim = claims.get("token_usage");
        return TokenUsageType.toEnum(tokenUsageClaim);
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
    }

}
