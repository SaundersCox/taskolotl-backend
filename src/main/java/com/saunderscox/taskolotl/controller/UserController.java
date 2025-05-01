//package com.saunderscox.taskolotl.controller;
//
//import com.saunderscox.taskolotl.dto.UserResponseDto;
//import java.util.List;
//import java.util.UUID;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//  @GetMapping
//  @PreAuthorize("hasRole('ADMIN')")  // Only admins can list all users
//  public List<UserResponseDto> getAllUsers() {
//    // ...
//  }
//
//  @GetMapping("/{id}")
//  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")  // Admin or self
//  public UserResponseDto getUser(@PathVariable UUID id) {
//    // ...
//  }
//}