package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @Size(max = 1000, message = "Profile description cannot exceed 1000 characters")
  private String profileDescription;

  @URL(message = "Profile picture must be a valid URL")
  private String profilePictureUrl;
}
