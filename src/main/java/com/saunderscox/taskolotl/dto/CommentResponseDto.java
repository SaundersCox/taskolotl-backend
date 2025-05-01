package com.saunderscox.taskolotl.dto;

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
public class CommentResponseDto extends BaseDto {

  private UUID authorId;
  private UUID boardItemId;
  private String description;
  private Set<String> tags;
}