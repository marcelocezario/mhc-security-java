package br.dev.mhc.security.dtos;

import br.dev.mhc.security.entities.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO implements Serializable {

    @EqualsAndHashCode.Include
    private final UUID id;
    private final String username;
    private final String password;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    @Builder.Default
    private final Set<String> roles = new HashSet<>();

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        password = user.getPassword();
        active = user.isActive();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();
        roles = user.getRoles();
    }

    public User toEntity() {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .active(active)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .roles(roles)
                .build();
    }

}
