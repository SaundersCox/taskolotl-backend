package com.saunderscox.taskolotl.entity;

import lombok.Getter;

/**
 * Represents the stages of study progression for a concept or skill. Tracks the journey from
 * initial awareness to mastery and innovation.
 */
@Getter
public enum StudyStage {
  UNAWARE("Not yet aware of the skill or knowledge area"),
  AWARE("Aware of the skill but haven't started learning"),
  LEARNING("Actively studying and practicing the skill"),
  APPLYING("Using the skill with guidance in real situations"),
  PROFICIENT("Can use the skill independently in most situations"),
  TEACHING("Mastered the skill and can teach others"),
  INNOVATING("Creating new approaches or extending the knowledge area");

  private final String description;

  StudyStage(String description) {
    this.description = description;
  }

  /**
   * Checks if this study stage is at or beyond the specified stage.
   *
   * @param stage The stage to compare against
   * @return true if this stage is equal to or more advanced than the specified stage
   */
  public boolean isAtLeast(StudyStage stage) {
    return this.ordinal() >= stage.ordinal();
  }

  /**
   * Checks if this study stage indicates ability to teach others.
   *
   * @return true if at TEACHING stage or beyond
   */
  public boolean canTeach() {
    return isAtLeast(TEACHING);
  }

  /**
   * Returns the next study stage in the progression.
   *
   * @return the next stage or null if already at the highest stage
   */
  public StudyStage next() {
    int nextOrdinal = this.ordinal() + 1;
    if (nextOrdinal < values().length) {
      return values()[nextOrdinal];
    }
    return null;
  }
}