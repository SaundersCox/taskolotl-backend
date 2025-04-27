package com.saunderscox.taskolotl.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public Mono<ResponseEntity<String>> handleOptimisticLockingFailure(
      OptimisticLockingFailureException ex) {
    return Mono.just(new ResponseEntity<>(
        "The data was modified by another user. Please try again.",
        HttpStatus.CONFLICT));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<String>> handleGenericException(Exception ex) {
    return Mono.just(new ResponseEntity<>(
        "An unexpected error occurred.",
        HttpStatus.INTERNAL_SERVER_ERROR));
  }
}