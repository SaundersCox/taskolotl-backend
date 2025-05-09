package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BoardItemCreateRequestDto {

  @NotNull(message = "Board item owner ID is required")
  private UUID ownerId;

  @NotBlank(message = "Title is required")
  @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
  private String title;

  @Size(max = 1000, message = "Description cannot exceed 1000 characters")
  private String description;

  private Integer position;

  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code (e.g., #FF5733)")
  private String color;

  @NotNull(message = "Board ID is required")
  private UUID boardId;

  private UUID skillId;
  private UUID conceptId;
}