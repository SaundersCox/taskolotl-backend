package com.saunderscox.taskolotl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saunderscox.taskolotl.service.JwtService;
import com.saunderscox.taskolotl.service.OAuth2UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final OAuth2UserService oauth2UserService;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Value("${spring.profiles.active:}")
  private String activeProfile;

  @Value("${app.security.cors.allowed-origins:}")
  private String allowedOrigins;

  /**
   * Configures CSRF, sessions, authorization rules, OAuth2 login, JWT authentication, and other
   *
   * @param http The {@link HttpSecurity} object to configure
   * @return The configured {@link SecurityFilterChain}
   * @throws Exception If an error occurs configuring the security chain
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    boolean isDev = activeProfile.contains("dev");

    return http
        .cors(cors -> {
          CorsConfiguration config = new CorsConfiguration();
          config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
          config.applyPermitDefaultValues();
          config.setAllowCredentials(true);

          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", config);
          cors.configurationSource(source);
        })
        .headers(headers -> {
          if (isDev) {
            // Specifically for H2 Console iframes
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
          }
        })
        .csrf(csrf -> {
          CsrfConfigurer<HttpSecurity> configurer = csrf
              .ignoringRequestMatchers("/api/**")
              .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
          if (isDev) {
            configurer.ignoringRequestMatchers("/h2-console/**");
          }
        })
        .sessionManagement(session -> session
            // Avoid sessions since we use JWT for stateless auth
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
            .successHandler((request, response, authentication) -> {
              response.setContentType("application/json");
              response.setHeader("Cache-Control", "no-store");
              response.setHeader("Pragma", "no-cache");

              String token = jwtService.generateToken(authentication);
              objectMapper.writeValue(response.getWriter(), objectMapper.createObjectNode()
                  .put("access_token", token)
                  .put("token_type", "Bearer")
                  .put("expires_in", jwtService.getExpirationMs() / 1000));
            }))
        .authorizeHttpRequests(auth -> {
          // Always permitted paths
          auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/login",
              "/oauth2/authorization/google", "/api/auth/status", "/error").permitAll();
          // Conditionally permitted paths
          if (isDev) {
            auth.requestMatchers("/h2-console/**").permitAll();
          }
          // Admin paths
          auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
          // All other paths
          auth.anyRequest().authenticated();
        })
        .build();
  }
}