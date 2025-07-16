package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipCreateRequest {

  @NotNull(message = "Self ID is required")
  private UUID selfId;

  @NotNull(message = "Target ID is required")
  private UUID targetId;
}
