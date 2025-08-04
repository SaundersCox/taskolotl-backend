package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.config.security.TokenProps;
import com.saunderscox.taskolotl.dto.AuthResponse;
import com.saunderscox.taskolotl.dto.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Service for JWT token operations and user authentication.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final TokenProps tokenProps;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;

  private long refreshTokenExpiration;

  private JwtBuilder createBaseJwt(UUID userId, String tokenType) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(Date.from(now))
        .issuer("https://taskolotl.com")
        .claim("type", tokenType)
        .claim("jti", UUID.randomUUID().toString())
        .signWith(getSigningKey());
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(tokenProps.getJwtSecret());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String generateAccessToken(UUID userId) {
    return createBaseJwt(userId, "access")
        .expiration(Date.from(Instant.now().plus(tokenProps.getAccessTokenExpiration(), ChronoUnit.MILLIS)))
        .compact();
  }

  private String generateRefreshToken(UUID userId) {
    return createBaseJwt(userId, "refresh")
        .expiration(Date.from(Instant.now().plus(tokenProps.getRefreshTokenExpiration(), ChronoUnit.MILLIS)))
        .compact();
  }

  public AuthResponse userIdToAuthResponse(UUID userId) {
    return new AuthResponse(generateAccessToken(userId), generateRefreshToken(userId));
  }

  @Transactional(readOnly = true)
  public UserResponse getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) throw new BadCredentialsException("No authenticated user found");
    if ("anonymousUser".equals(auth.getPrincipal())) throw new BadCredentialsException("User is not authenticated");

    try {
      UUID userId = UUID.fromString(auth.getName());
      return userService.getUserById(userId);

    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException("Invalid user ID format");
    }
  }

  @Transactional(readOnly = true)
  public AuthResponse refreshAccessToken(String refreshToken) {
    try {
      Claims claims = getClaims(refreshToken, "refresh");
      UUID userId = UUID.fromString(claims.getSubject());
      return userIdToAuthResponse(userId);

    } catch (Exception e) {
      throw new BadCredentialsException("Invalid refresh token: " + e.getMessage());
    }
  }

  public Claims getClaims(String token, String tokenType) {
    Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    String encodedTokenType = (String) claims.get("type");
    if (!tokenType.equals(encodedTokenType))
      throw new BadCredentialsException("Token type mismatch - expected: {}, provided: {}");
    return claims;
  }
}
