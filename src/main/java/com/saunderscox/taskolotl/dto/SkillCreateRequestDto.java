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
public class SkillCreateRequestDto {

  @NotBlank(message = "Skill name is required")
  @Size(max = 100, message = "Skill name cannot exceed 100 characters")
  private String name;

  @Size(max = 500, message = "Skill description cannot exceed 500 characters")
  private String description;
}