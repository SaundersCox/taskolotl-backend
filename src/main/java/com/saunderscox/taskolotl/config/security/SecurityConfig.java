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
 * Handles authentication and authorization
 * <ul>
 *   <li>CORS to allow frontend access</li>
 *   <li>CSRF disabled because API is stateless</li>
 *   <li>Frames enabled in dev to access H2 Console</li>
 *   <li>Validates auth code from the OAuth2 provider, finds or creates a user, and returns tokens</li>
 *   <li>Signs and parses tokens with secret & HMAC-SHA256 symmetric key</li>
 *   <li>Allows endpoints for auth, health, docs, </li>
 * </ul> OAuth2, and authorization using Spring Security's built-in JWT support.
 * Ensures sessions are stateless.
 */
@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

  private final SuccessHandler successHandler;
  private final TokenProps tokenProps;
  private final TokenFilter tokenFilter;

  @Value("${spring.profiles.active}")
  private String activeProfile;

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
        .oauth2Login(oauth2 -> oauth2
            .successHandler(successHandler))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.decoder(jwtDecoder())))
        .addFilterBefore(tokenFilter, BasicAuthenticationFilter.class)
        // Authorization
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/api/auth/**", "/actuator/health", "/swagger-ui/**", "/v3/api-docs/**")
              .permitAll();
          if (isDev) {
            auth.requestMatchers("/h2-console/**").permitAll();
          }
          auth.requestMatchers("/api/**").authenticated();
        })
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] keyBytes = Decoders.BASE64.decode(tokenProps.getJwtSecret());
    SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(secretKey).build();
  }
}
