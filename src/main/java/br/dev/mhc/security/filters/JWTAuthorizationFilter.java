package br.dev.mhc.security.filters;

import br.dev.mhc.security.constants.RouteConstants;
import br.dev.mhc.security.enums.TokenUsageType;
import br.dev.mhc.security.exceptions.InvalidTokenException;
import br.dev.mhc.security.services.AuthService;
import br.dev.mhc.security.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Map<String, TokenUsageType> restrictedRoutes = Map.of(
            RouteConstants.AUTH_ROUTE + "/refresh-token", TokenUsageType.REFRESH_TOKEN
    );
    private final AuthService authService;
    private final TokenService tokenService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, AuthService authService, TokenService tokenService) {
        super(authenticationManager);
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (isNull(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace("Bearer ", "");

        var extractedTokenType = tokenService.extractTokenUsageType(token);
        String uri = request.getRequestURI();

        var requiredTokenType = restrictedRoutes.getOrDefault(uri, TokenUsageType.ACCESS_TOKEN);

        try {
            if (!Objects.equals(requiredTokenType, extractedTokenType)) {
                throw new InvalidTokenException("Invalid token type");
            }
            var authentication = authService.authenticateWithToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
