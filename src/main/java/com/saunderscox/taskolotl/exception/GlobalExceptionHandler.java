package com.saunderscox.taskolotl.exception;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleAllExceptions(Exception ex, WebRequest request) {
    log.error("Unhandled exception occurred", ex);

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
    );
    problem.setProperty("timestamp", LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    log.warn("Type mismatch for parameter: {}", ex.getName());

    String message = ex.getRequiredType() == UUID.class
        ? "Invalid UUID format for parameter '" + ex.getName() + "': " + ex.getValue()
        : ex.getName() + " should be a valid " + ex.getRequiredType().getSimpleName();

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    problem.setProperty("timestamp", LocalDateTime.now());
    return problem;
  }
}