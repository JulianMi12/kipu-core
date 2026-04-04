package com.kipu.core.identity.infrastructure.security;

import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

  private final SecretKey signingKey;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;
  private final RefreshTokenRepository refreshTokenRepository;

  public JwtTokenProviderAdapter(
      @Value("${kipu.security.jwt.secret}") String secret,
      @Value("${kipu.security.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
      @Value("${kipu.security.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs,
      RefreshTokenRepository refreshTokenRepository) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public AuthTokens generate(User user) {
    Instant now = Instant.now();
    Instant accessExpiry = now.plusMillis(accessTokenExpirationMs);
    Instant refreshExpiry = now.plusMillis(refreshTokenExpirationMs);

    String accessToken = buildToken(user, now, accessExpiry);
    String refreshToken = buildToken(user, now, refreshExpiry);

    OffsetDateTime refreshTokenExpiresAt = refreshExpiry.atOffset(ZoneOffset.UTC);
    return new AuthTokens(accessToken, refreshToken, refreshTokenExpiresAt);
  }

  @Override
  public boolean isAccessTokenValid(String token) {
    try {
      Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  @Override
  public boolean isRefreshTokenValid(String token) {
    try {
      Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
    return refreshTokenRepository.existsValidToken(token);
  }

  @Override
  public UUID extractUserId(String token) {
    String subject =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    return UUID.fromString(subject);
  }

  private String buildToken(User user, Instant issuedAt, Instant expiresAt) {
    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey)
        .compact();
  }
}
