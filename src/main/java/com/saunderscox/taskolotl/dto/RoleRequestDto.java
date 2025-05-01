package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name cannot exceed 100 characters")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @Size(max = 100, message = "Hub cannot exceed 100 characters")
  private String hub;

  @Size(max = 100, message = "Team cannot exceed 100 characters")
  private String team;
}