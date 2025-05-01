package com.saunderscox.taskolotl.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    String endpoint = queryString != null ? uri + "?" + queryString : uri;

    logger.info("Request: {} {}", method, endpoint);

    // Store start time in request attribute for calculating duration
    request.setAttribute("startTime", System.currentTimeMillis());

    return true; // Continue with the request
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    // Calculate request duration
    Long startTime = (Long) request.getAttribute("startTime");
    if (startTime != null) {
      long duration = System.currentTimeMillis() - startTime;
      logger.info("Response: {} completed in {}ms", request.getRequestURI(), duration);
    }

    if (ex != null) {
      logger.error("Exception during request processing: {}", ex.getMessage());
    }
  }
}