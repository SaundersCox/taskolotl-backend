package com.saunderscox.taskolotl.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
  USER("USER"),
  MODERATOR("MODERATOR"),
  ADMIN("ADMIN");

  private final String name;

  Permission(String name) {
    this.name = name;
  }

  @Override
  public String getAuthority() {
    return name;
  }
}
