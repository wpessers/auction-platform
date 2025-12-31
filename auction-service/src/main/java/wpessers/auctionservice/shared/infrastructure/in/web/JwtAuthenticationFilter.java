package wpessers.auctionservice.shared.infrastructure.in.web;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import wpessers.auctionservice.shared.application.port.out.TokenParser;
import wpessers.auctionservice.shared.application.port.out.UserClaims;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /* It may be a little bit overkill to use a TokenParser port here, since it's difficult to
    loosely couple the filter code without over-engineering in my opinion. Hence, there's some very
    tight coupling with JWT here. For example catching the JwtException. This would be thrown when
    an invalid token is provided. Because the JwtTokenProviderAdapter uses the parseSignedClaims
    method, which throws when the token is expired, malformed, etc. Clearing the security context
    here will then eventually result in a 401 response when Spring security checks the context.*/

    private final TokenParser tokenParser;

    public JwtAuthenticationFilter(TokenParser tokenParser) {
        this.tokenParser = tokenParser;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserClaims claims = tokenParser.parseToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims,
                    null,
                    Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
