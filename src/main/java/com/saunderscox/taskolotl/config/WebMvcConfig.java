package com.saunderscox.taskolotl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final RequestLoggingInterceptor requestLoggingInterceptor;

  public WebMvcConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
    this.requestLoggingInterceptor = requestLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestLoggingInterceptor)
        .addPathPatterns("/**")  // Apply to all paths
        .excludePathPatterns("/actuator/**", "/h2-console/**");
  }
}