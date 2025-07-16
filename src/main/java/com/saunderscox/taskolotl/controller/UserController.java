package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.UserCreateRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.dto.UserUpdateRequest;
import com.saunderscox.taskolotl.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {

  private final UserService userService;

  @Operation(summary = "Get all users (Admin only)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getAllUsers(pageable));
  }

  @Operation(summary = "Get user by ID")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @Operation(summary = "Get user by OAuth2 ID")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @GetMapping("/oauth/{oauthId}")
  public ResponseEntity<UserResponse> getUserByOauthId(@PathVariable String oauthId) {
    return ResponseEntity.ok(userService.getUserByOauthId(oauthId));
  }

  @Operation(summary = "Get current user profile")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }

  @Operation(summary = "Create new user (Admin only)")
  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest dto) {
    return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
  }

  @Operation(summary = "Update user (Admin or self)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id, @Valid @RequestBody UserUpdateRequest dto) {
    return ResponseEntity.ok(userService.updateUser(id, dto));
  }

  @Operation(summary = "Delete user (Admin only)")
  @ApiResponse(responseCode = "204", description = "No content")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Search users by query")
  @ApiResponse(responseCode = "200", description = "Success")
  @GetMapping("/search")
  public ResponseEntity<Page<UserResponse>> searchUsers(
      @RequestParam String query, Pageable pageable) {
    return ResponseEntity.ok(userService.searchUsers(query, pageable));
  }

  @Operation(summary = "Add skill to user (Admin or self)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @PutMapping("/{id}/skills/{skillId}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponse> addSkillToUser(
      @PathVariable UUID id, @PathVariable UUID skillId) {
    return ResponseEntity.ok(userService.addSkillToUser(id, skillId));
  }

  @Operation(summary = "Remove skill from user (Admin or self)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @DeleteMapping("/{id}/skills/{skillId}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponse> removeSkillFromUser(
      @PathVariable UUID id, @PathVariable UUID skillId) {
    return ResponseEntity.ok(userService.removeSkillFromUser(id, skillId));
  }

  @Operation(summary = "Add role to user (Admin only)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @PutMapping("/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> addRoleToUser(
      @PathVariable UUID id, @PathVariable UUID roleId) {
    return ResponseEntity.ok(userService.addRoleToUser(id, roleId));
  }

  @Operation(summary = "Remove role from user (Admin only)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @DeleteMapping("/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> removeRoleFromUser(
      @PathVariable UUID id, @PathVariable UUID roleId) {
    return ResponseEntity.ok(userService.removeRoleFromUser(id, roleId));
  }
}
