package com.saunderscox.taskolotl.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.service.AuthService;
import com.saunderscox.taskolotl.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthService authService;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

    if (!(authentication instanceof OAuth2AuthenticationToken oauth2Token)) {
      log.error("Invalid authentication type");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication type");
      return;
    }

    OAuth2User oauth2User = oauth2Token.getPrincipal();
    String provider = oauth2Token.getAuthorizedClientRegistrationId();

    String oauthId = provider.equals("google") ? oauth2User.getAttribute("sub") : null;
    String email = oauth2User.getAttribute("email");
    String name = oauth2User.getAttribute("name");

    if (oauthId == null || email == null) {
      log.error("Missing user info for provider: {}", provider);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user info for provider: " + provider);
      return;
    }

    try {
      User user = userService.findOrCreateOAuth2User(oauthId, email, name, provider);
      UUID userId = user.getId();

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      objectMapper.writeValue(response.getWriter(), authService.userIdToAuthResponse(userId));

      log.info("Authenticated [name:{}][email:{}][id:{}][provider:{}]",
          name, email, userId, provider);
    } catch (Exception e) {
      log.error("Error processing OAuth2 authentication", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication processing failed");
    }
  }

}
