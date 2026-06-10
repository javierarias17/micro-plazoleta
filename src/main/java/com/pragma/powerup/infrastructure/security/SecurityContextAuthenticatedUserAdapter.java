package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextAuthenticatedUserAdapter implements IAuthenticatedUserPort {

    @Override
    public Long getAuthenticatedUserId() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getDetails();
    }
}
