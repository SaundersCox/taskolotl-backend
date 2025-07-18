package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BoardUpdateRequest {

  @Size(max = 100, message = "Board title cannot exceed 100 characters")
  private String title;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  private Boolean visible;

  private Set<UUID> ownerIds;
  private Set<UUID> memberIds;
  private Set<UUID> boardItemIds;
  private Set<UUID> roleIds;
  private Set<UUID> skillIds;
}
