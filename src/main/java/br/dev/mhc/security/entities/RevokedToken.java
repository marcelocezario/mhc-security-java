package br.dev.mhc.security.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table
public class RevokedToken implements Serializable {

    @Id
    @Column(name = "token_uuid", nullable = false)
    private UUID tokenUuid;
    @Column(name = "user_id", nullable = false)private UUID userId;
    @CreationTimestamp
    @Column(name = "revoked_at", nullable = false, updatable = false)
    private Instant revokedAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

}
