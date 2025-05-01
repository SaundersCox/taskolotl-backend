package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequestDto {

  @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
  private String description;

  private Set<String> tags;
}