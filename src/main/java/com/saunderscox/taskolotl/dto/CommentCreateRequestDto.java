package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequestDto {

  @NotNull(message = "Author ID is required")
  private UUID authorId;

  @NotNull(message = "Board item ID is required")
  private UUID boardItemId;

  @NotBlank(message = "Comment description is required")
  @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
  private String description;

  private Set<String> tags;
}