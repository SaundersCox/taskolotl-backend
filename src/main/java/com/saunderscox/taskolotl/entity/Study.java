package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a study item on a board with study-specific properties like study stage, resources,
 * and mentor.
 */
@Entity
@DiscriminatorValue("STUDY")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Study extends BoardItem {

  @Enumerated(EnumType.STRING)
  @Column(name = "study_stage", nullable = false)
  @Setter
  private StudyStage studyStage = StudyStage.UNAWARE;

  @Column(length = 2000)
  @Setter
  private String resources;

  @ManyToOne
  @Setter
  private User mentor;

  /**
   * Advances the study to the next stage.
   *
   * @return true if advanced, false if already at final stage
   */
  public boolean advanceStage() {
    if (studyStage == null || studyStage == StudyStage.INNOVATING) {
      return false;
    }

    StudyStage nextStage = studyStage.next();
    if (nextStage != null) {
      studyStage = nextStage;
      return true;
    }
    return false;
  }

  /**
   * Moves the study back to the previous stage.
   *
   * @return true if moved back, false if already at first stage
   */
  public boolean regressStage() {
    if (studyStage == null || studyStage == StudyStage.UNAWARE) {
      return false;
    }

    int prevOrdinal = studyStage.ordinal() - 1;
    if (prevOrdinal >= 0) {
      studyStage = StudyStage.values()[prevOrdinal];
      return true;
    }
    return false;
  }

  /**
   * Marks the study as mastered by setting the stage to INNOVATING.
   */
  public void markMastered() {
    studyStage = StudyStage.INNOVATING;
  }

  /**
   * Checks if this study is at a teaching level.
   *
   * @return true if at TEACHING stage or beyond
   */
  public boolean canTeach() {
    return studyStage != null && studyStage.canTeach();
  }

  /**
   * Checks if this study is at least at the specified stage.
   *
   * @param stage The stage to compare against
   * @return true if at or beyond the specified stage
   */
  public boolean isAtLeast(StudyStage stage) {
    return studyStage != null && studyStage.isAtLeast(stage);
  }
}