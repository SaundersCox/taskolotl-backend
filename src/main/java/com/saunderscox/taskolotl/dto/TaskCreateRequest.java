package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.TaskStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TaskCreateRequest extends BoardItemCreateRequest {

  private TaskStage taskStage;
  private Instant dueDate;
  private Float estimatedHours;
  private UUID assigneeId;
  private boolean important;
}
