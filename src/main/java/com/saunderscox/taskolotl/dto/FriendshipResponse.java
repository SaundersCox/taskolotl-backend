package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FriendshipResponse extends BaseResponse {

  private UUID selfId;
  private UUID targetId;
  private FriendshipStatus friendshipStatus;
  private Boolean blocked;
  private String notes;
}
