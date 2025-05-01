package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a task item on a board with task-specific properties like task stage, completion
 * status, and due date.
 */
@Entity
@DiscriminatorValue("TASK")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Task extends BoardItem {

  @Enumerated(EnumType.STRING)
  @Column(name = "task_stage", nullable = false)
  @Builder.Default
  @Setter
  private TaskStage taskStage = TaskStage.BACKLOG;

  @Setter
  private Instant dueDate;

  @Column(name = "completed_at")
  @Setter
  private Instant completedAt;

  @Column(name = "estimated_hours")
  @Setter
  private Float estimatedHours;

  @Column(name = "actual_hours")
  @Setter
  private Float actualHours;

  @ManyToOne
  @Setter
  private User assignee;

  @Column(name = "is_important")

  @Builder.Default
  @Setter
  private boolean important = false;

  /**
   * Advances the task to the next stage in the workflow. If the task reaches DONE stage, sets the
   * completedAt timestamp.
   *
   * @return true if advanced, false if already at final stage
   */
  public boolean advanceStage() {
    if (taskStage == null || taskStage == TaskStage.DONE) {
      return false;
    }

    TaskStage nextStage = taskStage.next();
    if (nextStage != null) {
      taskStage = nextStage;

      if (taskStage == TaskStage.DONE) {
        completedAt = Instant.now();
      }

      return true;
    }
    return false;
  }

  /**
   * Moves the task back to the previous stage in the workflow. If moving from DONE, clears the
   * completedAt timestamp.
   *
   * @return true if moved back, false if already at first stage
   */
  public boolean regressStage() {
    if (taskStage == null || taskStage == TaskStage.BACKLOG) {
      return false;
    }

    boolean wasCompleted = taskStage == TaskStage.DONE;

    TaskStage prevStage = taskStage.previous();
    if (prevStage != null) {
      taskStage = prevStage;

      if (wasCompleted) {
        completedAt = null;
      }

      return true;
    }
    return false;
  }

  /**
   * Marks the task as complete by setting stage to DONE and recording completion time.
   */
  public void markComplete() {
    taskStage = TaskStage.DONE;
    completedAt = Instant.now();
  }

  /**
   * Checks if the task is overdue based on its due date.
   *
   * @return true if the task has a due date in the past and is not completed
   */
  public boolean isOverdue() {
    return dueDate != null &&
        Instant.now().isAfter(dueDate) &&
        (taskStage != TaskStage.DONE);
  }

  /**
   * Gets the remaining time until the due date.
   *
   * @return Duration until due date, or null if no due date or already overdue
   */
  public Duration getTimeRemaining() {
    if (dueDate == null || isOverdue()) {
      return null;
    }
    return Duration.between(Instant.now(), dueDate);
  }

  /**
   * Calculates the efficiency ratio (estimated vs actual hours). Values less than 1 indicate the
   * task took longer than estimated.
   *
   * @return efficiency ratio or null if missing data
   */
  public Float getEfficiencyRatio() {
    if (estimatedHours != null && actualHours != null && actualHours > 0) {
      return estimatedHours / actualHours;
    }
    return null;
  }

  /**
   * Marks the task as important.
   */
  public void markAsImportant() {
    this.important = true;
  }

  /**
   * Marks the task as standard (not important).
   */
  public void markAsStandard() {
    this.important = false;
  }

  /**
   * Toggles the importance of the task.
   *
   * @return the new importance state
   */
  public boolean toggleImportance() {
    this.important = !this.important;
    return this.important;
  }
}