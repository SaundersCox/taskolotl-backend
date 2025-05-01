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
public class TaskCreateRequestDto extends BoardItemCreateRequestDto {

  private TaskStage taskStage;
  private Instant dueDate;
  private Float estimatedHours;
  private UUID assigneeId;
  private boolean important;
}