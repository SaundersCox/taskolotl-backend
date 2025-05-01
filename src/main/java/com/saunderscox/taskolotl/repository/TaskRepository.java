package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.Task;
import com.saunderscox.taskolotl.entity.TaskStage;
import com.saunderscox.taskolotl.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Task} entities. Extends BoardItemRepository to inherit common
 * board item operations.
 */
@Repository
public interface TaskRepository extends BoardItemRepository<Task> {

  /**
   * Finds all tasks with a specific stage.
   *
   * @param taskStage the task stage
   * @return list of tasks with the stage
   */
  List<Task> findByTaskStage(TaskStage taskStage);

  /**
   * Finds all tasks assigned to a specific user.
   *
   * @param assignee the assignee
   * @return list of tasks assigned to the user
   */
  List<Task> findByAssignee(User assignee);

  /**
   * Finds all tasks assigned to a specific user ID.
   *
   * @param assigneeId the assignee ID
   * @return list of tasks assigned to the user
   */
  List<Task> findByAssigneeId(UUID assigneeId);

  /**
   * Finds all tasks with due dates before a specific time.
   *
   * @param dueDate the due date threshold
   * @return list of tasks due before the specified time
   */
  List<Task> findByDueDateBefore(Instant dueDate);

  /**
   * Finds all important tasks.
   *
   * @return list of important tasks
   */
  List<Task> findByImportantTrue();

  /**
   * Finds all completed tasks (with completedAt not null).
   *
   * @return list of completed tasks
   */
  List<Task> findByCompletedAtNotNull();

  /**
   * Finds all incomplete tasks (with completedAt null).
   *
   * @return list of incomplete tasks
   */
  List<Task> findByCompletedAtNull();

  /**
   * Finds all tasks with a specific stage and due date before a specific time.
   *
   * @param taskStage the task stage
   * @param dueDate   the due date threshold
   * @return list of tasks matching the criteria
   */
  List<Task> findByTaskStageAndDueDateBefore(TaskStage taskStage, Instant dueDate);

  /**
   * Finds all tasks assigned to a user with a specific stage.
   *
   * @param assignee  the assignee
   * @param taskStage the task stage
   * @return list of tasks matching the criteria
   */
  List<Task> findByAssigneeAndTaskStage(User assignee, TaskStage taskStage);

  /**
   * Finds all tasks in a board with a specific stage.
   *
   * @param board     the board
   * @param taskStage the task stage
   * @return list of tasks matching the criteria
   */
  List<Task> findByBoardAndTaskStage(Board board, TaskStage taskStage);

  /**
   * Finds all important tasks assigned to a user.
   *
   * @param assignee the assignee
   * @return list of important tasks assigned to the user
   */
  List<Task> findByAssigneeAndImportantTrue(User assignee);

  /**
   * Finds all tasks with pagination.
   *
   * @param taskStage the task stage
   * @param pageable  pagination information
   * @return page of tasks with the specified stage
   */
  Page<Task> findByTaskStage(TaskStage taskStage, Pageable pageable);
}