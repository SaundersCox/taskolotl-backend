package com.saunderscox.taskolotl.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saunderscox.taskolotl.entity.Permission;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.repository.UserRepository;
import com.saunderscox.taskolotl.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final AuthService authService;
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
    String accessToken = authService.generateAccessToken(user);
    String refreshToken = authService.generateRefreshToken(user);
    writeTokenResponse(response, accessToken, refreshToken);
  }

  private User findOrCreateUser(OAuth2User oauth2User, String providerName) {
    Map<String, Object> attributes = oauth2User.getAttributes();
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String oauthId = (String) attributes.get("sub");
    String picture = (String) attributes.get("picture");

    User user = userRepository.findByOauthId(oauthId)
        .map(existingUser -> {
          log.info("User authenticated with email {}", email);
          return existingUser;
        })
        .orElseGet(() -> {
          log.info("User created & authenticated with email: {}", email);
          return userRepository.save(User.builder()
              .email(email)
              .username(name)
              .oauthId(oauthId)
              .oauthProvider(providerName)
              .profilePictureUrl(picture)
              .permission(Permission.USER)
              .build());
        });

    // Update profile picture if changed
    if (!Objects.equals(picture, user.getProfilePictureUrl())) {
      user.setProfilePictureUrl(picture);
      userRepository.save(user);
    }

    return user;
  }

  private void writeTokenResponse(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
    response.setContentType("application/json");
    response.setHeader("Cache-Control", "no-store");

    objectMapper.writeValue(response.getWriter(), Map.of(
        "access_token", accessToken,
        "refresh_token", refreshToken,
        "token_type", "Bearer",
        "expires_in", 900,
        "refresh_expires_in", 604800
    ));
  }
}
