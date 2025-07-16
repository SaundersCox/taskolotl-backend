package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BoardResponse extends BaseResponse {

  private String title;
  private BoardType boardType;
  private String description;
  private boolean visible;
  private Set<UUID> ownerIds;
  private Set<UUID> memberIds;
  private Set<UUID> boardItemIds;
  private Set<UUID> roleIds;
  private Set<UUID> skillIds;
}
