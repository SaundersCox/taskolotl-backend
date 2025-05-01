package com.saunderscox.taskolotl.dto;

import java.util.List;
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
public abstract class BoardItemResponseDto extends BaseDto {

  private UUID ownerId;
  private String title;
  private String description;
  private Integer position;
  private String color;
  private List<UUID> commentIds;
  private UUID boardId;
  private UUID skillId;
  private UUID conceptId;
}