package br.dev.mhc.security.services;

import java.time.Instant;
import java.util.UUID;

public interface RevokedTokenService {

    void revokeToken(UUID tokenUuid, UUID userId, Instant expiresAt);

    boolean isTokenRevoked(UUID tokenUuid);

}
