package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.TaskStage;
import java.time.Instant;
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
public class TaskResponseDto extends BoardItemResponseDto {

  private TaskStage taskStage;
  private Instant dueDate;
  private Instant completedAt;
  private Float estimatedHours;
  private Float actualHours;
  private UUID assigneeId;
  private boolean important;
}