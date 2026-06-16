package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String MSG_NO_AUTH_TOKEN = "No authentication token provided.";
    static final String MESSAGE_SECURITY = "message_security";

    private final JwtTokenAdapter jwtTokenAdapter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            request.setAttribute(MESSAGE_SECURITY, MSG_NO_AUTH_TOKEN);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            String email = jwtTokenAdapter.getEmailFromToken(token);
            String role = jwtTokenAdapter.getRoleFromToken(token);
            Long userId = jwtTokenAdapter.getUserIdFromToken(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
            authentication.setDetails(userId);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidTokenException e) {
            request.setAttribute(MESSAGE_SECURITY, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
