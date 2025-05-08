package com.saunderscox.taskolotl.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class PageableParameterConfig {

  @Bean
  public OperationCustomizer pageableOperationCustomizer() {
    return (operation, handlerMethod) -> {
      // Check if the method has a Pageable parameter
      if (hasPageableParameter(handlerMethod)) {
        // Add page parameter
        operation.addParametersItem(
            new Parameter()
                .in(ParameterIn.QUERY.toString())
                .name("page")
                .description("Page number (0-based)")
                .example("0")
                .schema(new IntegerSchema())
                .required(false)
        );

        // Add size parameter
        operation.addParametersItem(
            new Parameter()
                .in(ParameterIn.QUERY.toString())
                .name("size")
                .description("Number of items per page")
                .example("10")
                .schema(new IntegerSchema())
                .required(false)
        );

        // Add sort parameter
        operation.addParametersItem(
            new Parameter()
                .in(ParameterIn.QUERY.toString())
                .name("sort")
                .description(
                    "Sorting criteria in format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
                .example("createdAt,desc")
                .schema(new StringSchema())
                .required(false)
        );
      }
      return operation;
    };
  }

  private boolean hasPageableParameter(HandlerMethod handlerMethod) {
    return java.util.Arrays.stream(handlerMethod.getMethodParameters())
        .anyMatch(parameter -> Pageable.class.isAssignableFrom(parameter.getParameterType()));
  }
}