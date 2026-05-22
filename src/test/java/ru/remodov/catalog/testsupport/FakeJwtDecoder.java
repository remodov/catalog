package ru.remodov.catalog.testsupport;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

public class FakeJwtDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        String[] parts = token.split(":", 2);
        if (parts.length != 2) {
            throw new JwtException("Test token must be in format <role>:<uuid>: " + token);
        }
        String role = parts[0];
        UUID subject;
        try {
            subject = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Test token sub is not a UUID: " + parts[1]);
        }
        Instant now = Instant.now();
        Map<String, Object> headers = Map.of("alg", "none");
        Map<String, Object> claims = Map.of(
            "sub", subject.toString(),
            "iss", "test-issuer",
            "iat", now,
            "exp", now.plusSeconds(3600),
            "realm_access", Map.of("roles", List.of(role))
        );
        return new Jwt(token, now, now.plusSeconds(3600), headers, claims);
    }
}
