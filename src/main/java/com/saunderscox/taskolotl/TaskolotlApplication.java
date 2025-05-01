package com.saunderscox.taskolotl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaskolotlApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaskolotlApplication.class, args);
  }

}
