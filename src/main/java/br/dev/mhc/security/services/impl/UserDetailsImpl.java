package br.dev.mhc.security.services.impl;

import br.dev.mhc.security.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class UserDetailsImpl implements UserDetails {

    @Getter
    private final UUID tokenUuid;
    @Getter
    private final User user;
    private final String username;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(User user, UUID tokenUuid) {
        super();
        this.tokenUuid = tokenUuid;
        this.user = user;
        username = user.getUsername();
        password = user.getPassword();
        active = user.isActive();
        authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public UUID getUserId() {
        return nonNull(user) ? user.getId() : null;
    }
}
