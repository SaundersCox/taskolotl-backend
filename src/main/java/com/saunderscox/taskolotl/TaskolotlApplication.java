package com.saunderscox.taskolotl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@Slf4j
@EnableConfigurationProperties
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableCaching
public class TaskolotlApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaskolotlApplication.class, args);
  }
}
