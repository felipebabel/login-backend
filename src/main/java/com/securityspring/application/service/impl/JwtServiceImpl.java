package com.securityspring.application.service.impl;

import java.util.Date;
import java.util.HashMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.securityspring.application.service.api.ConfigServiceApi;
import com.securityspring.application.service.api.JwtServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.ProjectProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Key;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@Scope("prototype")
public class JwtServiceImpl implements JwtServiceApi {

    static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

    private static final long REFRESH_TOKEN_VALIDITY_MS = 1000 * 60 * 60 * 24;

    private final ProjectProperties projectProperties;

    private static final long CACHE_TTL_MS = TimeUnit.MINUTES.toMillis(5);

    private final ConfigServiceApi configService;

    private Long cachedExpirationValue = null;

    private long lastCacheTime = 0L;

    public JwtServiceImpl(ProjectProperties projectProperties, ConfigServiceApi configService) {
        this.projectProperties = projectProperties;
        this.configService = configService;
    }

    @Override
    public TokenVO generateToken(final UserVO userVO) {
        long accessTokenValidity = getAccessTokenValidity();

        final String accessToken = Jwts.builder()
                .claim("role", userVO.getRole().name())
                .setSubject(userVO.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        final String refreshToken = Jwts.builder()
                .setSubject(userVO.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return TokenVO.builder().token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenValidity) .build();
    }

    @Override
    public TokenVO refreshToken(String oldRefreshToken, UserVO userVO) {
        if (isTokenExpired(oldRefreshToken)) {
            LOGGER.warn("Refresh token expired");
            return null;
        }

        String username = extractUsername(oldRefreshToken);
        String role = userVO.getRole().name();

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        long accessTokenValidity = getAccessTokenValidity();

        String newAccessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return TokenVO.builder()
                .token(newAccessToken)
                .refreshToken(oldRefreshToken)
                .expiresIn(accessTokenValidity)
                .build();
    }


    private long getAccessTokenValidity() {
        long now = System.currentTimeMillis();
        if (cachedExpirationValue == null || (now - lastCacheTime) > CACHE_TTL_MS) {
            try {
                ConfigVO config = configService.getConfig("tokenSessionExpiration");
                cachedExpirationValue = Long.parseLong(config.getValue()) * 60 * 1000;
                lastCacheTime = now;
                LOGGER.info("Token expiration loaded from DB: {} ms", cachedExpirationValue);
            } catch (Exception e) {
                LOGGER.error("Failed to load tokenSessionExpiration, using default 10min", e);
                cachedExpirationValue = 10 * 60 * 1000L;
            }
        }
        return cachedExpirationValue;
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.projectProperties.getProperty("jwt.secret"));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    @Override
    public String extractClaimByName(String token, String claimName) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(claimName, String.class);
    }

}
