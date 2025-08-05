package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.AuthResponse;
import com.saunderscox.taskolotl.dto.RefreshTokenRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization API")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

    var authResponse = authService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
    return ResponseEntity.ok(authResponse);
  }

  @GetMapping("/me")
  @Operation(summary = "Get current user profile")
  public ResponseEntity<UserResponse> getCurrentUser() {

    UserResponse userResponse = authService.getCurrentUser();
    return ResponseEntity.ok(userResponse);
  }
}
