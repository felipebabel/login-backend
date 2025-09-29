package com.securityspring.infrastructure.config;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class TokenJwtUtil {

    private static final String SECRET_KEY = "MinhaChaveSuperSecretaMuitoLongaParaJWT12345"; //todo env

    public String generateVerificationToken(final Long userId, final String email) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        Date expiration = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("type", "EMAIL_VERIFICATION")
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long validateVerificationToken(final String token) throws JwtException {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            String type = claims.get("type", String.class);
            if (!"EMAIL_VERIFICATION".equals(type)) {
                throw new JwtException("Invalid token type");
            }
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expired", e);
        } catch (JwtException e) {
            throw new JwtException("Invalid token", e);
        }
    }

}
