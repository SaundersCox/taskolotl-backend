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
        .csrf(csrf -> csrf
            // API (Stateless) & H2 Console don't need CSRF protection
            .ignoringRequestMatchers("/api/**", isDev ? "/h2-console/**" : "")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
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
        .authorizeHttpRequests(auth -> auth
            // Swagger UI, error page, login, OAuth2 flow endpoints
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/error", "/login",
                "/oauth2/authorization/google", isDev ? "/h2-console/**" : "").permitAll()
            // Admin-specific endpoints
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            // Other API endpoints and Actuator
            .anyRequest().authenticated())
        .build();
  }
}