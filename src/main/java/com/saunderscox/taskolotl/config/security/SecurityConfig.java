package com.saunderscox.taskolotl.config.security;

import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

/**
 * Configures CORS, CSRF, OAuth2, and authorization using Spring Security's built-in JWT support.
 * Ensures sessions are stateless.
 */
@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

  private final TokenFilter tokenFilter;
  private final SuccessHandler successHandler;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(secretKey).build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    log.info("Initializing SecurityFilterChain with active profile: {}", activeProfile);
    boolean isDev = activeProfile.contains("dev");

    return http
        // Permit frontend / Disable CSRF / Stateless Sessions
        .cors(cors -> cors
            .configurationSource(request -> new CorsConfiguration()
                .applyPermitDefaultValues()
                .setAllowedOriginPatterns(List.of(isDev ? "http://localhost:*" : "https://taskolotl.com"))
            ))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers
            .frameOptions(isDev ?
                HeadersConfigurer.FrameOptionsConfig::sameOrigin :
                HeadersConfigurer.FrameOptionsConfig::disable)
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Authentication
        .addFilterBefore(tokenFilter, BasicAuthenticationFilter.class)
        .oauth2Login(oauth2 -> oauth2
            .successHandler(successHandler))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.decoder(jwtDecoder())))
        // Authorization
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/api/auth/**", "/actuator/health", "/error", "/swagger-ui/**", "/v3/api-docs/**")
              .permitAll();
          if (isDev) {
            auth.requestMatchers("/h2-console/**").permitAll();
          }
          auth.requestMatchers("/api/**").authenticated();
        })
        .build();
  }
}
