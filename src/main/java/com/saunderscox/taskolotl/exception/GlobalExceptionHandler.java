package com.saunderscox.taskolotl.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * Handle exceptions thrown during API calls and convert them into error responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
    return createProblemDetail(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
  }

  @ExceptionHandler(LazyInitializationException.class)
  public ProblemDetail handleLazyInitialization(LazyInitializationException ex) {
    return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        "Data loading error occurred", ex);
  }

  // All other cases:
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleAllExceptions(Exception ex, WebRequest request) {
    return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred", ex);
  }

  private ProblemDetail createProblemDetail(HttpStatus status, String detail, Exception ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setProperty("timestamp", LocalDateTime.now());

    if (status.is4xxClientError()) {
      log.warn("4xx Error: {}", problem);
    } else if (status.is5xxServerError()) {
      log.error("5xx Error: {}", problem, ex);
    } else {
      log.info("Other error: {}", problem);
    }

    return problem;
  }
}
