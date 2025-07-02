package com.saunderscox.taskolotl.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties(prefix = "app.security.jwt")
@RequiredArgsConstructor
@Slf4j
@Setter
public class JwtService {

  private final JwtEncoder jwtEncoder;
  private final String[] googleClaimKeys = new String[]{"name", "email", "picture"};

  private long expirationMs;

  public String generateToken(Authentication authentication) {
    var user = (OAuth2User) authentication.getPrincipal();
    var token = (OAuth2AuthenticationToken) authentication;
    String provider = token.getAuthorizedClientRegistrationId();
    String roles = authentication
        .getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
    Instant now = Instant.now();

    JwtClaimsSet claims = JwtClaimsSet.builder()
        .subject(user.getName())
        .issuedAt(now)
        .expiresAt(now.plusMillis(expirationMs))
        .claim("roles", roles)
        .claims(map -> {
          String[] claimKeys = {};
          if (provider.equalsIgnoreCase("google")) {
            claimKeys = googleClaimKeys;
          }
          log.info("Adding claims from {}: {}", provider, claimKeys);
          for (String key : claimKeys) {
            map.put(key, Objects.requireNonNull(user.getAttribute(key)));
          }
        })
        .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}
