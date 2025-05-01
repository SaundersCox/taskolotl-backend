package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUpdateRequestDto {

  @Size(max = 100, message = "Board title cannot exceed 100 characters")
  private String title;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  private Boolean visible;
}