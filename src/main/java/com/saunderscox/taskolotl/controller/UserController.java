package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.UserCreateRequestDto;
import com.saunderscox.taskolotl.dto.UserResponseDto;
import com.saunderscox.taskolotl.dto.UserUpdateRequestDto;
import com.saunderscox.taskolotl.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getAllUsers(pageable));
  }

  @Operation(summary = "Get user by ID")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @Operation(summary = "Get user by OAuth2 ID")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @GetMapping("/oauth/{oauthId}")
  public ResponseEntity<UserResponseDto> getUserByOauthId(@PathVariable String oauthId) {
    return ResponseEntity.ok(userService.getUserByOauthId(oauthId));
  }

  @Operation(summary = "Get current user profile")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }

  @Operation(summary = "Create new user (Admin only)")
  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto dto) {
    return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
  }

  @Operation(summary = "Update user (Admin or self)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
  public ResponseEntity<UserResponseDto> updateUser(
      @PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDto dto) {
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
  public ResponseEntity<Page<UserResponseDto>> searchUsers(
      @RequestParam String query, Pageable pageable) {
    return ResponseEntity.ok(userService.searchUsers(query, pageable));
  }

  @Operation(summary = "Add skill to user (Admin or self)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @PutMapping("/{id}/skills/{skillId}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponseDto> addSkillToUser(
      @PathVariable UUID id, @PathVariable UUID skillId) {
    return ResponseEntity.ok(userService.addSkillToUser(id, skillId));
  }

  @Operation(summary = "Remove skill from user (Admin or self)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @DeleteMapping("/{id}/skills/{skillId}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponseDto> removeSkillFromUser(
      @PathVariable UUID id, @PathVariable UUID skillId) {
    return ResponseEntity.ok(userService.removeSkillFromUser(id, skillId));
  }

  @Operation(summary = "Add role to user (Admin only)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @PutMapping("/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDto> addRoleToUser(
      @PathVariable UUID id, @PathVariable UUID roleId) {
    return ResponseEntity.ok(userService.addRoleToUser(id, roleId));
  }

  @Operation(summary = "Remove role from user (Admin only)")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
  @DeleteMapping("/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDto> removeRoleFromUser(
      @PathVariable UUID id, @PathVariable UUID roleId) {
    return ResponseEntity.ok(userService.removeRoleFromUser(id, roleId));
  }
}