package br.dev.mhc.security.repositories;

import br.dev.mhc.security.entities.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {

    boolean existsByTokenUuid(UUID tokenUuid);

}
