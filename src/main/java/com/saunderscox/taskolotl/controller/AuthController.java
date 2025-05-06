//package com.saunderscox.taskolotl.controller;
//
//import com.saunderscox.taskolotl.service.JwtService;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class AuthController {
//
//  private final JwtService jwtService;
//
//  @GetMapping("/public/auth/status")
//  public Map<String, Object> getAuthStatus() {
//    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//    boolean authenticated = auth != null && auth.isAuthenticated() &&
//        !"anonymousUser".equals(auth.getPrincipal());
//
//    Map<String, Object> status = new HashMap<>();
//    status.put("authenticated", authenticated);
//    if (authenticated) {
//      status.put("username", auth.getName());
//    }
//    return status;
//  }
//
//  @GetMapping("/user/profile")
//  public Map<String, Object> getUserProfile() {
//    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//    return Map.of(
//        "username", auth.getName(),
//        "authorities", auth.getAuthorities()
//    );
//  }
//}