package br.dev.mhc.security.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static java.util.Objects.isNull;

@Configuration
@ConfigurationProperties(prefix = "auth.token")
@Getter
@Setter
public class TokenProperties {

    private String secret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    @PostConstruct
    public void validateSecret() {
        if (isNull(secret) || secret.isBlank()) {
            throw new IllegalStateException("⚠️ Mandatory property 'auth.token.secret' is not defined. Configure in the application.properties of the main application.");
        }
    }

}
