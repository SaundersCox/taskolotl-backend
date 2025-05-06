package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.UserCreateRequestDto;
import com.saunderscox.taskolotl.dto.UserResponseDto;
import com.saunderscox.taskolotl.dto.UserUpdateRequestDto;
import com.saunderscox.taskolotl.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getAllUsers(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDto> createUser(
      @Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
    return new ResponseEntity<>(userService.createUser(userCreateRequestDto), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponseDto> updateUser(
      @PathVariable UUID id,
      @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
    return ResponseEntity.ok(userService.updateUser(id, userUpdateRequestDto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<Page<UserResponseDto>> searchUsers(@RequestParam String query,
      Pageable pageable) {
    return ResponseEntity.ok(userService.searchUsers(query, pageable));
  }

  @PutMapping("/{id}/skills/{skillId}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponseDto> addSkillToUser(
      @PathVariable UUID id,
      @PathVariable UUID skillId) {
    return ResponseEntity.ok(userService.addSkillToUser(id, skillId));
  }

  @DeleteMapping("/{id}/skills/{skillId}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<UserResponseDto> removeSkillFromUser(
      @PathVariable UUID id,
      @PathVariable UUID skillId) {
    return ResponseEntity.ok(userService.removeSkillFromUser(id, skillId));
  }

  @PutMapping("/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDto> addRoleToUser(
      @PathVariable UUID id,
      @PathVariable UUID roleId) {
    return ResponseEntity.ok(userService.addRoleToUser(id, roleId));
  }

  @DeleteMapping("/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDto> removeRoleFromUser(
      @PathVariable UUID id,
      @PathVariable UUID roleId) {
    return ResponseEntity.ok(userService.removeRoleFromUser(id, roleId));
  }
}