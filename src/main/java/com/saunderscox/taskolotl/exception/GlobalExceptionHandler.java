package com.saunderscox.taskolotl.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    String message = "Database constraint violation";
    log.debug("Database constraint violation: {}", ex.getMessage());

    if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
      String constraintName = constraintEx.getConstraintName();
      String sqlMessage = constraintEx.getSQLException().getMessage();

      // Handle unique constraint violations
      if (constraintName != null && constraintName.toUpperCase().contains("UK_")) {
        Pattern pattern = Pattern.compile("(?:.*\\.)?UK_([^_]+)(?:_INDEX.*)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(constraintName);

        if (matcher.matches()) {
          String fieldName = matcher.group(1).toLowerCase();
          message = "A record with this " + fieldName + " already exists";
        } else {
          message = "A record with these values already exists";
        }
      }
      // Handle foreign key constraint violations (can't be caught by DTO validation)
      else if (sqlMessage != null && (
        sqlMessage.contains("foreign key constraint") ||
          sqlMessage.contains("FOREIGN KEY") ||
          constraintName != null && constraintName.toUpperCase().contains("FK_"))) {
        message = "Cannot modify this record because it is referenced by other records";
      }
      // Handle check constraints (complex business rules that might not be in DTO validation)
      else if (constraintName != null && constraintName.toUpperCase().contains("CK_")) {
        message = "The provided values don't meet the required business rules";
      }
    }

    return createProblemDetail(HttpStatus.CONFLICT, message, ex);
  }


  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    String errorMessage = ex.getBindingResult().getAllErrors().stream()
      .map(error -> {
        if (error instanceof FieldError fieldError) {
          return fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return error.getDefaultMessage();
      })
      .collect(Collectors.joining(", "));

    ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, errorMessage, ex);

    return ResponseEntity.of(problemDetail).build();
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
