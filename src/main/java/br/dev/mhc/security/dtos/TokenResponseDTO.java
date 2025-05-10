package br.dev.mhc.security.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@RequiredArgsConstructor
@Getter
@Builder
public class TokenResponseDTO implements Serializable {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final long createdAt;
    private final long expiresIn;
    @Builder.Default
    private final List<String> userPendingIssues = new ArrayList<>();

}
