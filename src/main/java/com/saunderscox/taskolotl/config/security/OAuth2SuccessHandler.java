package com.saunderscox.taskolotl.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtEncoder jwtEncoder;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("taskolotl")
        .issuedAt(now)
        .expiresAt(now.plus(1, ChronoUnit.HOURS))
        .subject(authentication.getName())
        .claim("authorities", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList())
        .build();

    response.setContentType("application/json");
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragma", "no-cache");

    objectMapper.writeValue(response.getWriter(), Map.of(
        "access_token", jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(),
        "token_type", "Bearer",
        "expires_in", 3600
    ));
  }
}
