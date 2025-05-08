package com.saunderscox.taskolotl.config;

import com.saunderscox.taskolotl.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = jwtService.extractToken(request);
    log.debug("JWT Filter processing request to {}", request.getRequestURI());

    if (token != null) {
      log.debug("Token found in request");
      if (jwtService.validateToken(token)) {
        log.debug("Token is valid, setting authentication");
        SecurityContextHolder.getContext().setAuthentication(
            jwtService.getAuthentication(token));
      } else {
        log.debug("Token validation failed");
      }
    } else {
      log.debug("No token found in request");
    }

    filterChain.doFilter(request, response);
  }
}