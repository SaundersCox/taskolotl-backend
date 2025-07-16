package com.saunderscox.taskolotl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class BoardItemResponse extends BaseResponse {

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
