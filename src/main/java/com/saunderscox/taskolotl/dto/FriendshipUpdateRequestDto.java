package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.FriendshipStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipUpdateRequestDto {

  private FriendshipStatus friendshipStatus;
  private Boolean blocked;

  @Size(max = 500, message = "Notes cannot exceed 500 characters")
  private String selfNotes;

  @Size(max = 500, message = "Notes cannot exceed 500 characters")
  private String targetNotes;
}