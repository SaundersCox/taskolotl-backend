package com.saunderscox.taskolotl.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipCreateRequestDto {

  @NotNull(message = "Self ID is required")
  private UUID selfId;

  @NotNull(message = "Target ID is required")
  private UUID targetId;
}