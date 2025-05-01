package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.StudyStage;
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
public class StudyCreateRequestDto extends BoardItemCreateRequestDto {

  private StudyStage studyStage;
  private String resources;
  private UUID mentorId;
}