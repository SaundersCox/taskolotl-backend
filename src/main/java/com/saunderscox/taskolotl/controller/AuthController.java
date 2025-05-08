package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.service.AuthenticationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationService authService;

  @GetMapping("/status")
  public Map<String, Object> getAuthStatus() {
    return authService.getAuthenticationStatus();
  }
}