package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.mapper.UserMapper;
import com.saunderscox.taskolotl.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for JWT token operations and user authentication.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Value("${jwt.secret}")
  private String jwtSecret;

  /**
   * Checks if the current user is authenticated.
   */
  public boolean isAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.isAuthenticated() && !isAnonymousUser(auth.getPrincipal());
  }

  /**
   * Retrieves the currently authenticated user.
   */
  @Transactional(readOnly = true)
  public UserResponse getCurrentUser() {
    if (!isAuthenticated()) {
      throw new AuthenticationException("No authenticated user found") {
      };
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = UUID.fromString(auth.getName());
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new AuthenticationException("User not found") {
        });
    return userMapper.toResponseDto(user);
  }

  /**
   * Generates a JWT access token (15 min expiry).
   */
  public String generateAccessToken(User user) {
    return createBaseJwtBuilder(user, "access")
        .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
        .claim("email", user.getEmail())
        .claim("permission", user.getPermission().name())
        .compact();
  }

  /**
   * Generates a JWT refresh token (7 day expiry).
   */
  public String generateRefreshToken(User user) {
    return createBaseJwtBuilder(user, "refresh")
        .expiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
        .compact();
  }

  /**
   * Refreshes an access token using a valid refresh token.
   */
  @Transactional(readOnly = true)
  public Map<String, Object> refreshAccessToken(String refreshToken) {
    try {
      Claims claims = validateRefreshToken(refreshToken);
      User user = findUserFromClaims(claims);

      String newAccessToken = generateAccessToken(user);
      String newRefreshToken = generateRefreshToken(user);

      return createTokenResponse(newAccessToken, newRefreshToken);

    } catch (Exception e) {
      throw new AuthenticationException("Invalid refresh token") {
      };
    }
  }

  /**
   * Creates OAuth2-compliant token response.
   */
  public Map<String, Object> createTokenResponse(String accessToken, String refreshToken) {
    Map<String, Object> response = new HashMap<>();
    response.put("access_token", accessToken);
    response.put("refresh_token", refreshToken);
    response.put("token_type", "Bearer");
    response.put("expires_in", 900);
    response.put("refresh_expires_in", 604800);
    return response;
  }

  public SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Creates base JWT builder with common claims.
   */
  private JwtBuilder createBaseJwtBuilder(User user, String tokenType) {
    Instant now = Instant.now();
    return Jwts.builder()
        .issuer("taskolotl-api")
        .issuedAt(Date.from(now))
        .subject(user.getId().toString())
        .claim("type", tokenType)
        .claim("jti", UUID.randomUUID().toString())
        .signWith(getSigningKey());
  }

  /**
   * Validates a refresh token and returns its claims.
   */
  private Claims validateRefreshToken(String refreshToken) {
    Claims claims = Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(refreshToken)
        .getPayload();

    if (!"refresh".equals(claims.get("type"))) {
      throw new AuthenticationException("Invalid token type") {
      };
    }

    return claims;
  }

  /**
   * Finds a user from JWT claims.
   */
  private User findUserFromClaims(Claims claims) {
    UUID userId = UUID.fromString(claims.getSubject());
    return userRepository.findById(userId)
        .orElseThrow(() -> new AuthenticationException("User not found") {
        });
  }

  private boolean isAnonymousUser(Object principal) {
    return "anonymousUser".equals(principal);
  }
}
