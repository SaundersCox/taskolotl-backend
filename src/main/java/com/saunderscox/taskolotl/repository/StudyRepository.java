package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.Study;
import com.saunderscox.taskolotl.entity.StudyStage;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Study} entities. Extends BoardItemRepository to inherit common
 * board item operations.
 */
@Repository
public interface StudyRepository extends BoardItemRepository<Study> {

  /**
   * Finds all studies with a specific stage.
   *
   * @param studyStage the study stage
   * @return list of studies with the stage
   */
  List<Study> findByStudyStage(StudyStage studyStage);

  /**
   * Finds all studies with a specific mentor.
   *
   * @param mentor the mentor
   * @return list of studies mentored by the user
   */
  List<Study> findByMentor(User mentor);

  /**
   * Finds all studies with a specific mentor ID.
   *
   * @param mentorId the mentor ID
   * @return list of studies mentored by the user
   */
  List<Study> findByMentorId(UUID mentorId);

  /**
   * Finds all studies containing specific resources text.
   *
   * @param resourcesText the text to search for in resources
   * @return list of studies with matching resources
   */
  List<Study> findByResourcesContainingIgnoreCase(String resourcesText);

  /**
   * Finds all studies with a specific stage and mentor.
   *
   * @param studyStage the study stage
   * @param mentor     the mentor
   * @return list of studies matching the criteria
   */
  List<Study> findByStudyStageAndMentor(StudyStage studyStage, User mentor);

  /**
   * Finds all studies in a board with a specific stage.
   *
   * @param board      the board
   * @param studyStage the study stage
   * @return list of studies matching the criteria
   */
  List<Study> findByBoardAndStudyStage(Board board, StudyStage studyStage);

  /**
   * Finds all studies with resources containing text and a specific stage.
   *
   * @param resourcesText the text to search for in resources
   * @param studyStage    the study stage
   * @return list of studies matching the criteria
   */
  List<Study> findByResourcesContainingIgnoreCaseAndStudyStage(String resourcesText,
      StudyStage studyStage);

  /**
   * Finds all studies with pagination.
   *
   * @param studyStage the study stage
   * @param pageable   pagination information
   * @return page of studies with the specified stage
   */
  Page<Study> findByStudyStage(StudyStage studyStage, Pageable pageable);
}