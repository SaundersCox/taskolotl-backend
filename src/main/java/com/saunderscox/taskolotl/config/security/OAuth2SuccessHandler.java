package com.saunderscox.taskolotl.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saunderscox.taskolotl.entity.Permission;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtEncoder jwtEncoder;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
    OAuth2User oauth2User = oauth2Token.getPrincipal();
    String providerName = oauth2Token.getAuthorizedClientRegistrationId();

    User user = findOrCreateUser(oauth2User, providerName);
    String jwt = generateJwt(user);
    writeTokenResponse(response, jwt);
  }

  private User findOrCreateUser(OAuth2User oauth2User, String providerName) {
    Map<String, Object> attributes = oauth2User.getAttributes();
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String oauthId = (String) attributes.get("sub");
    String picture = (String) attributes.get("picture");

    User user = userRepository.findByOauthId(oauthId)
        .orElseGet(() -> {
          User newUser = User.builder()
              .email(email)
              .username(name)
              .oauthId(oauthId)
              .oauthProvider(providerName)
              .profilePictureUrl(picture)
              .permission(Permission.USER)
              .build();
          return userRepository.save(newUser);
        });

    // Update profile picture if changed
    if (!Objects.equals(picture, user.getProfilePictureUrl())) {
      user.setProfilePictureUrl(picture);
      userRepository.save(user);
    }

    return user;
  }

  private String generateJwt(User user) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("taskolotl-api")
        .issuedAt(now)
        .expiresAt(now.plus(15, ChronoUnit.MINUTES))
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("permission", user.getPermission().name())
        .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  private void writeTokenResponse(HttpServletResponse response, String jwt) throws IOException {
    response.setContentType("application/json");
    response.setHeader("Cache-Control", "no-store");

    objectMapper.writeValue(response.getWriter(), Map.of(
        "access_token", jwt,
        "token_type", "Bearer",
        "expires_in", 900
    ));
  }
}
