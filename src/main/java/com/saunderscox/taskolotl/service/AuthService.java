package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for handling user authentication status and JWT token operations.
 *
 * <p>Provides methods to check current authentication state, retrieve user information,
 * and generate access/refresh tokens for authenticated users. Used primarily for
 * OAuth2-based authentication flows and JWT token management.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtEncoder jwtEncoder;
  private final UserRepository userRepository;

  // Authentication Status Methods
  public Map<String, Object> getAuthenticationStatus() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean authenticated = auth != null
        && auth.isAuthenticated()
        && !"anonymousUser".equals(auth.getPrincipal());

    Map<String, Object> status = new HashMap<>();
    status.put("authenticated", authenticated);
    if (authenticated) {
      status.put("userId", auth.getName());
      getCurrentUser().ifPresent(user -> {
        status.put("oauthId", user.getOauthId());
        status.put("oauthProvider", user.getOauthProvider());
        status.put("email", user.getEmail());
        status.put("username", user.getUsername());
        status.put("permission", user.getPermission().name());
        status.put("profilePictureUrl", user.getProfilePictureUrl());
      });
    }
    return status;
  }

  public Optional<User> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
      return Optional.empty();
    }

    try {
      UUID userId = UUID.fromString(auth.getName());
      return userRepository.findById(userId);
    } catch (IllegalArgumentException e) {
      // Log the error if needed
      return Optional.empty();
    }
  }

  public boolean isAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null
        && auth.isAuthenticated()
        && !"anonymousUser".equals(auth.getPrincipal());
  }

  // Token Generation Methods
  public String generateAccessToken(User user) {
    if (user == null || user.getId() == null) {
      throw new IllegalArgumentException("User and user ID cannot be null");
    }

    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("taskolotl-api")
        .issuedAt(now)
        .expiresAt(now.plus(15, ChronoUnit.MINUTES))
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("permission", user.getPermission().name())
        .claim("type", "access")
        .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  public String generateRefreshToken(User user) {
    if (user == null || user.getId() == null) {
      throw new IllegalArgumentException("User and user ID cannot be null");
    }

    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("taskolotl-api")
        .issuedAt(now)
        .expiresAt(now.plus(7, ChronoUnit.DAYS))
        .subject(user.getId().toString())
        .claim("type", "refresh")
        .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  public Map<String, Object> createTokenResponse(String accessToken, String refreshToken) {
    Map<String, Object> response = new HashMap<>();
    response.put("access_token", accessToken);
    response.put("refresh_token", refreshToken);
    response.put("token_type", "Bearer");
    response.put("expires_in", 900); // 15 minutes
    response.put("refresh_expires_in", 604800); // 7 days
    return response;
  }
}
