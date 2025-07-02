package com.saunderscox.taskolotl.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class SecurityProperties {

  private Security security = new Security();

  @Getter
  @Setter
  public static class Security {

    private Cors cors = new Cors();
    private Jwt jwt = new Jwt();
    private List<String> adminEmails;
  }

  @Getter
  @Setter
  public static class Cors {

    private List<String> allowedOrigins;
  }

  @Getter
  @Setter
  public static class Jwt {

    private String secret;
    private long expirationMs = 86400000; // Default: 24 hours
    private String issuer = "https://plmcty.com/taskolotl";
    private String audience = "taskolotl-web-client";
    private Map<String, Provider> providers = new HashMap<>();
  }

  @Getter
  @Setter
  public static class Provider {

    private List<String> claimKeys;
  }
}
