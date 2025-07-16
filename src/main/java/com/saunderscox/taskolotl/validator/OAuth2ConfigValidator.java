package com.saunderscox.taskolotl.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuth2ConfigValidator {

  @EventListener(ApplicationReadyEvent.class)
  public void validateConfig() {
    String clientId = System.getenv("GOOGLE_CLIENT_ID");
    String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");

    if (clientId == null || clientSecret == null) {
      log.error("Missing OAuth2 environment variables!");
    }

    if (!clientId.endsWith(".apps.googleusercontent.com")) {
      log.error("Invalid Google Client ID format: {}", clientId);
    }

    log.info("OAuth2 Config - Client ID: {}...", clientId.substring(0, 10));
  }
}
