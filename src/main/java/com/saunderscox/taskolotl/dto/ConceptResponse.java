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
public class ConceptResponse extends BaseResponse {

  private String name;
  private String description;
  private UUID skillId;
  private List<UUID> boardItemIds;
}
