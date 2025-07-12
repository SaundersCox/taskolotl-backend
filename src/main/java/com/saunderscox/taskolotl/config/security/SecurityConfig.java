package com.saunderscox.taskolotl.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configures CORS, CSRF, OAuth2, and authorization using Spring Security's built-in JWT support.
 * Ensures sessions are stateless.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final JwtDecoder jwtDecoder;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    boolean isDev = activeProfile.contains("dev");

    return http
        // Permit frontend / Disable CSRF / Stateless Sessions
        .cors(cors -> cors
            .configurationSource(request -> {
              var config = new CorsConfiguration();
              config.setAllowedOriginPatterns(List.of(isDev ? "http://localhost:*" : "https://plmcty.com"));
              config.applyPermitDefaultValues();
              return config;
            }))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Authentication
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .oauth2Login(oauth2 -> oauth2
            .successHandler(oAuth2SuccessHandler))
        // Authorization
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/login",
                  "/oauth2/authorization/google", "/api/auth/status", "/error")
              .permitAll()
              .requestMatchers("/api/admin/**").hasRole("ADMIN");
          if (isDev) {
            auth.requestMatchers("/h2-console/**").permitAll();
          }
          auth.anyRequest().authenticated();
        })
        .build();
  }

}
