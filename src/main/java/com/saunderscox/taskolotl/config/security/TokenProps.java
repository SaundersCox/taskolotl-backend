package com.saunderscox.taskolotl.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.token")
@Getter
@Setter
public class TokenProps {

  private String jwtSecret;
  private String issuer;
  private Long accessTokenExpiration;
  private Long refreshTokenExpiration;
}
