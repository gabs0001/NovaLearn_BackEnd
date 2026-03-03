package br.com.novalearn.platform.core.security;

import br.com.novalearn.platform.domain.entities.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
public class JWTUtil {
    private final SecretKey secretKey;
    private final long accessTokenSeconds;
    private final long refreshTokenSeconds;
    private final long refreshTokenSecondsRememberMe;
    private final String base64Secret;

    public JWTUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration:900000}") long accessTokenSeconds,
            @Value("${security.jwt.refreshExpiration:2592000}") long refreshTokenSeconds,
            @Value("${security.jwt.refreshExpirationRememberMe:5184000}") long refreshTokenSecondsRememberMe
    ) {
        Objects.requireNonNull(secret, "JWT secret must be provided");

        this.base64Secret = Base64.getEncoder()
                .encodeToString(secret.getBytes(StandardCharsets.UTF_8));

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenSeconds = accessTokenSeconds;
        this.refreshTokenSeconds = refreshTokenSeconds;
        this.refreshTokenSecondsRememberMe = refreshTokenSecondsRememberMe;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenSeconds);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", nullSafe(user.getEmail().toString()))
                .claim("role", nullSafe(user.getRole().toString()))
                .claim("name", buildFullName(user))
                .claim("locale", nullSafe(user.getLocale()))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user, Long expiresSeconds) {
        long seconds = expiresSeconds != null
                ? expiresSeconds
                : refreshTokenSeconds;

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(seconds);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        try {
            return Long.parseLong(getClaims(token).getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return getClaims(token).getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isExpired(String token) {
        Date exp = extractExpiration(token);
        return exp == null || exp.before(new Date());
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenSeconds;
    }

    public long getRefreshTokenExpirationDefaultSeconds() {
        return refreshTokenSeconds;
    }

    public long getRefreshTokenExpirationRememberMeSeconds() {
        return refreshTokenSecondsRememberMe;
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenSeconds * 1000;
    }

    public long getRefreshTokenExpirationDefaultMs() {
        return refreshTokenSeconds * 1000;
    }

    public long getRefreshTokenExpirationRememberMeMs() {
        return refreshTokenSecondsRememberMe * 1000;
    }

    public String getBase64Secret() {
        return base64Secret;
    }

    private static String buildFullName(User user) {
        String first = nullSafe(user.getFirstName());
        String last = nullSafe(user.getLastName());

        if (first.isEmpty() && last.isEmpty()) return null;
        if (first.isEmpty()) return last;
        if (last.isEmpty()) return first;

        return first + " " + last;
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}