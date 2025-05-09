package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class BoardCreateRequestDto {

  @NotBlank(message = "Board title is required")
  @Size(max = 100, message = "Board title cannot exceed 100 characters")
  private String title;

  @NotNull(message = "Board type is required")
  private BoardType boardType;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  private boolean visible;

  @NotEmpty(message = "At least one owner is required")
  private Set<UUID> ownerIds;

  private Set<UUID> memberIds;
}