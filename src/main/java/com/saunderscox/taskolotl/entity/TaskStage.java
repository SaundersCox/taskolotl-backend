package com.saunderscox.taskolotl.entity;

import lombok.Getter;

/**
 * Represents the workflow stages a task can progress through. Tracks the journey from creation to
 * completion.
 */
@Getter
public enum TaskStage {
  BACKLOG("Task is planned but not yet started"),
  TODO("Task is ready to be worked on"),
  IN_PROGRESS("Task is actively being worked on"),
  REVIEW("Task is completed and awaiting review"),
  DONE("Task is completed and approved");

  private final String description;

  TaskStage(String description) {
    this.description = description;
  }

  /**
   * Checks if this stage is at or beyond the specified stage.
   *
   * @param stage The stage to compare against
   * @return true if this stage is equal to or more advanced than the specified stage
   */
  public boolean isAtLeast(TaskStage stage) {
    return this.ordinal() >= stage.ordinal();
  }

  /**
   * Checks if this stage indicates the task is actively being worked on.
   *
   * @return true if the task is in progress
   */
  public boolean isActive() {
    return this == IN_PROGRESS;
  }

  /**
   * Checks if this stage indicates the task is completed.
   *
   * @return true if the task is done
   */
  public boolean isCompleted() {
    return this == DONE;
  }

  /**
   * Checks if this stage indicates the task is awaiting review.
   *
   * @return true if the task is in review
   */
  public boolean isInReview() {
    return this == REVIEW;
  }

  /**
   * Returns the next stage in the workflow.
   *
   * @return the next stage or null if already at the final stage
   */
  public TaskStage next() {
    int nextOrdinal = this.ordinal() + 1;
    if (nextOrdinal < values().length) {
      return values()[nextOrdinal];
    }
    return null;
  }

  /**
   * Returns the previous stage in the workflow.
   *
   * @return the previous stage or null if already at the first stage
   */
  public TaskStage previous() {
    int prevOrdinal = this.ordinal() - 1;
    if (prevOrdinal >= 0) {
      return values()[prevOrdinal];
    }
    return null;
  }
}