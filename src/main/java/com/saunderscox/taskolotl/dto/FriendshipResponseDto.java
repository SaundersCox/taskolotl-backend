package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.FriendshipStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FriendshipResponseDto extends BaseDto {

  private UUID selfId;
  private UUID targetId;
  private FriendshipStatus friendshipStatus;
  private Boolean blocked;
  private String notes;
}