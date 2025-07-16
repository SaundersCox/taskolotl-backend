package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.RefreshTokenRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization API")
public class AuthController {

  private final AuthService authService;

  @GetMapping("/me")
  @PreAuthorize("@authService.isAuthenticated()")
  @Operation(summary = "Get current user profile")
  @ApiResponse(responseCode = "401", description = "Not authenticated")
  public ResponseEntity<UserResponse> getCurrentUser() {
    UserResponse userResponse = authService.getCurrentUser();
    return ResponseEntity.ok(userResponse);
  }

  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token")
  @ApiResponse(responseCode = "401", description = "Invalid refresh token")
  public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody RefreshTokenRequest request) {
    Map<String, Object> tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
    return ResponseEntity.ok(tokenResponse);
  }

}
