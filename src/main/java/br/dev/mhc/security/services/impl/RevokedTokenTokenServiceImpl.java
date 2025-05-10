package br.dev.mhc.security.services.impl;

import br.dev.mhc.security.entities.RevokedToken;
import br.dev.mhc.security.repositories.RevokedTokenRepository;
import br.dev.mhc.security.services.RevokedTokenService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class RevokedTokenTokenServiceImpl implements RevokedTokenService {

    private final RevokedTokenRepository repository;

    @Override
    public void revokeToken(UUID tokenUuid, UUID userId, Instant expiresAt) {
        requireNonNull(tokenUuid);
        requireNonNull(userId);
        requireNonNull(expiresAt);
        var revokedToken = RevokedToken.builder().tokenUuid(tokenUuid).userId(userId).expiresAt(expiresAt).build();
        repository.save(revokedToken);
    }

    @Override
    public boolean isTokenRevoked(UUID tokenUuid) {
        return repository.existsByTokenUuid(tokenUuid);
    }
}
