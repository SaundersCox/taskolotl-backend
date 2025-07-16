package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.StudyStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StudyResponse extends BoardItemResponse {

  private StudyStage studyStage;
  private String resources;
  private UUID mentorId;
}
