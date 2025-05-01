package com.saunderscox.taskolotl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saunderscox.taskolotl.service.CustomOAuth2UserService;
import com.saunderscox.taskolotl.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Configuration class for setting up Spring Security, including OAuth2 login and JWT token
 * handling.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Value("${spring.profiles.active:}")
  private String activeProfile;

  /**
   * Configures the security filter chain with custom rules for OAuth2 login and JWT token
   * handling.
   *
   * @param http the HttpSecurity object to configure
   * @return a SecurityFilterChain instance
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    if (activeProfile.contains("dev")) {
      http.authorizeHttpRequests(auth ->
              auth.requestMatchers("/h2-console/**").permitAll())
          .headers(headers ->
              headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
          );
    }

    http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/**")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.
            requestMatchers("/api/public/**", "/actuator/health/**", "/", "/error",
                "/login/**", "/oauth2/**")
            .permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo ->
                userInfo.userService(customOAuth2UserService))
            .successHandler(getSuccessHandler()))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Creates a custom authentication success handler for OAuth2 login that returns a JSON response
   * containing the JWT token.
   */
  private AuthenticationSuccessHandler getSuccessHandler() {
    return (request, response, authentication) -> {
      response.setContentType("application/json");

      String token = jwtService.generateToken(authentication);
      long expirationSeconds = jwtService.getExpirationMs() / 1000;
      ObjectNode value = objectMapper.createObjectNode()
          .put("access_token", token)
          .put("token_type", "Bearer")
          .put("expires_in", expirationSeconds);
      objectMapper.writeValue(response.getWriter(), value);
    };
  }
}
