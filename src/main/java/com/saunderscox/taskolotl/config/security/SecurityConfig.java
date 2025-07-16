package com.saunderscox.taskolotl.config.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Configures CORS, CSRF, OAuth2, and authorization using Spring Security's built-in JWT support.
 * Ensures sessions are stateless.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
    return NimbusJwtDecoder.withSecretKey(secretKey).build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    boolean isDev = activeProfile.contains("dev");

    return http
        // Permit frontend / Disable CSRF / Stateless Sessions
        .cors(cors -> cors
            .configurationSource(request -> new CorsConfiguration()
                .applyPermitDefaultValues()
                .setAllowedOriginPatterns(List.of(isDev ? "http://localhost:*" : "https://plmcty.com"))
            ))
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**"))
        .headers(headers -> headers
            .frameOptions(isDev ?
                HeadersConfigurer.FrameOptionsConfig::sameOrigin :
                HeadersConfigurer.FrameOptionsConfig::disable)
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Authentication
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .oauth2Login(oauth2 -> oauth2
            .successHandler(oAuth2SuccessHandler))
        // Authorization
        .authorizeHttpRequests(auth -> {
          // Public Endpoints (Must come first)
          auth.requestMatchers(
                  "/api/auth/**",
                  "/error",
                  "/login",
                  "/oauth2/authorization/google",
                  "/swagger-ui/**",
                  "/v3/api-docs/**"
              )
              .permitAll()
              // User Role Endpoints
              .requestMatchers("/api/admin/**").hasRole("ADMIN")
              .requestMatchers("/api/**").hasRole("USER");
          if (isDev) {
            auth.requestMatchers("/h2-console/**").permitAll();
          }
          auth.anyRequest().authenticated();
        })
        .build();
  }
}
