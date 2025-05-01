package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.BoardType;
import java.util.List;
import java.util.Set;
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
public class BoardResponseDto extends BaseDto {

  private String title;
  private BoardType boardType;
  private String description;
  private boolean visible;
  private Set<UUID> ownerIds;
  private Set<UUID> memberIds;
  private List<UUID> boardItemIds;
  private Set<UUID> roleIds;
  private Set<UUID> skillIds;
}