package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequest {

  @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
  private String description;

  private Set<String> tags;
}
