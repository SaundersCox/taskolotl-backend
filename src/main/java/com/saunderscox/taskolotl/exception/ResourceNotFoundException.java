package com.saunderscox.taskolotl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource cannot be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static ResourceNotFoundException forResource(String resourceType, Object resourceId) {
    return new ResourceNotFoundException(resourceType + " not found with ID: " + resourceId);
  }
}