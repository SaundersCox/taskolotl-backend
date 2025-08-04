package com.saunderscox.taskolotl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUser {
  private String id;
  private String email;
  private String name;
  private String pictureUrl;
  private boolean emailVerified;
}
