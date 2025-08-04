package com.saunderscox.taskolotl.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final LogInterceptor logInterceptor;

  public WebMvcConfig(LogInterceptor logInterceptor) {
    this.logInterceptor = logInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(logInterceptor)
        .addPathPatterns("/**")  // Apply to all paths
        .excludePathPatterns("/actuator/**", "/h2-console/**");
  }
}
