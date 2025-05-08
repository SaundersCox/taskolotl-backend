package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.entity.Permission;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom OAuth2 user service that integrates with our application's user management.
 * <p>
 * IMPORTANT: This service is automatically called by Spring Security during the OAuth2
 * authentication flow. You do not need to call this service directly from your controllers or other
 * services. Spring Security will invoke the loadUser method when a user authenticates through an
 * OAuth2 provider.
 * <p>
 * This service handles: 1. Finding existing users by OAuth ID 2. Creating new users when needed 3.
 * Updating profile information from the OAuth provider
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  /**
   * Loads the user from the OAuth2 provider and synchronizes with our database. This method is
   * automatically called by Spring Security during OAuth2 authentication.
   *
   * @param userRequest The OAuth2 user request containing provider details and tokens
   * @return The authenticated OAuth2User object
   * @throws OAuth2AuthenticationException If authentication fails
   */
  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // Get the authenticated user from the OAuth provider
    OAuth2User oauth2User = super.loadUser(userRequest);
    String providerName = userRequest.getClientRegistration().getRegistrationId();

    // Extract essential user details from OAuth provider attributes
    Map<String, Object> attributes = oauth2User.getAttributes();
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String oauthId = (String) attributes.get("sub");
    String picture = (String) attributes.get("picture");

    // Find existing user by OAuth ID or create a new one
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

    // Update profile information if it has changed
    boolean needsUpdate = false;

    if (picture != null && !picture.equals(user.getProfilePictureUrl())) {
      user.setProfilePictureUrl(picture);
      needsUpdate = true;
    }

    // Save only if changes were made
    if (needsUpdate) {
      userRepository.save(user);
    }

    return oauth2User;
  }
}