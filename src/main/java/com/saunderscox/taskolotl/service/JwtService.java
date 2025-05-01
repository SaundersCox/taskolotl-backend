package com.saunderscox.taskolotl.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service for handling JWT token operations including generation, validation, and extraction. This
 * service is specifically designed to work with OAuth2 authentication.
 */
@Service
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtService {

  /**
   * Secret key used for signing JWT tokens. This should be set in application properties and kept
   * secure.
   */
  private String secret;

  /**
   * Token expiration time in milliseconds. This should be set in application properties.
   */
  private long expirationMs;

  /**
   * Generates a JWT token from an OAuth2 authentication object. The token includes user details and
   * authorities from the OAuth2 user.
   *
   * @param authentication The OAuth2 authentication object containing user details
   * @return A signed JWT token string
   */
  public String generateToken(Authentication authentication) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    Date now = new Date();
    return Jwts.builder()
        .subject(oAuth2User.getName())
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationMs))
        .claim("roles", roles)
        .claim("name", oAuth2User.getAttribute("name"))
        .claim("email", oAuth2User.getAttribute("email"))
        .claim("picture", oAuth2User.getAttribute("picture"))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Extracts the JWT token from the Authorization header of an HTTP request. The token should be in
   * the format "Bearer [token]".
   *
   * @param request The HTTP request containing the Authorization header
   * @return The JWT token string if present and properly formatted, null otherwise
   */
  public String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")
        ? bearerToken.substring(7) : null;
  }

  /**
   * Validates a JWT token by verifying its signature and structure. Does not check expiration
   * separately as the parser will fail for expired tokens.
   *
   * @param token The JWT token string to validate
   * @return true if the token is valid, false if it's invalid or expired
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Creates an Authentication object from a valid JWT token. Extracts the subject (username) and
   * authorities (roles) from the token claims.
   *
   * @param token The JWT token string to extract authentication details from
   * @return An Authentication object containing the user's identity and authorities
   */
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();

    String username = claims.getSubject();
    String rolesString = claims.get("roles", String.class);

    List<SimpleGrantedAuthority> authorities =
        StringUtils.hasText(rolesString) ? java.util.Arrays.stream(rolesString.split(","))
            .filter(role -> !role.isEmpty()).map(SimpleGrantedAuthority::new).toList()
            : java.util.Collections.emptyList();

    return new UsernamePasswordAuthenticationToken(username, null, authorities);
  }

  /**
   * Creates a signing key from the configured secret. This key is used to sign and verify JWT
   * tokens.
   *
   * @return A SecretKey instance for JWT signing
   */
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
}