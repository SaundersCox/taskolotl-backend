package com.saunderscox.taskolotl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private static final String TOKEN_TYPE = "Bearer";
  private String accessToken;
  private String refreshToken;
}
