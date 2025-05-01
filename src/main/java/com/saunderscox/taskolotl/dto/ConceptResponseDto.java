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
public class ConceptResponseDto extends BaseDto {

  private String name;
  private String description;
  private UUID skillId;
  private List<UUID> boardItemIds;
}