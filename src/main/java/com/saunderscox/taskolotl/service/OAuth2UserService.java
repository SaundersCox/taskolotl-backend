package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.entity.Permission;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

/**
 * Handles OAuth2 user authentication by creating new users or updating existing ones.
 * Automatically called by Spring Security during the OAuth2 login flow.
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);
    String providerName = userRequest.getClientRegistration().getRegistrationId();

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

    if (!Objects.equals(picture, user.getProfilePictureUrl())) {
      user.setProfilePictureUrl(picture);
      userRepository.save(user);
    }

    return oauth2User;
  }
}
