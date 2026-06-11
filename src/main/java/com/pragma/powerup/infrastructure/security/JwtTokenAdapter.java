package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtTokenAdapter {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_ROLE_ID = "roleId";
    private static final String CLAIM_USER_ID = "userId";

    private static final String MSG_TOKEN_EXPIRED = "Token has expired. Please log in again.";
    private static final String MSG_TOKEN_SIGNATURE_INVALID = "Token signature is invalid.";
    private static final String MSG_TOKEN_FORMAT_INVALID = "Token format is invalid.";
    private static final String MSG_TOKEN_TYPE_UNSUPPORTED = "Token type is not supported.";
    private static final String MSG_TOKEN_EMPTY_OR_NULL = "Token is empty or null.";

    @Value("${jwt.secret}")
    private String secret;

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get(CLAIM_ROLE, String.class);
    }

    public Long getRoleIdFromToken(String token) {
        return getClaims(token).get(CLAIM_ROLE_ID, Long.class);
    }

    public Long getUserIdFromToken(String token) {
        return getClaims(token).get(CLAIM_USER_ID, Long.class);
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(MSG_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new InvalidTokenException(MSG_TOKEN_SIGNATURE_INVALID);
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException(MSG_TOKEN_FORMAT_INVALID);
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(MSG_TOKEN_TYPE_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(MSG_TOKEN_EMPTY_OR_NULL);
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
