package br.dev.mhc.security.services;

import java.util.UUID;

public interface RevokedTokenService {

    void revokeToken(UUID tokenUuid, UUID userId);

    boolean isTokenRevoked(UUID tokenUuid);

}
