package com.saunderscox.taskolotl.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  public Map<String, Object> getAuthenticationStatus() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean authenticated = auth != null
        && auth.isAuthenticated()
        && !"anonymousUser".equals(auth.getPrincipal());

    Map<String, Object> status = new HashMap<>();
    status.put("authenticated", authenticated);
    if (authenticated) {
      status.put("oauthId", auth.getName());
    }
    return status;
  }
}