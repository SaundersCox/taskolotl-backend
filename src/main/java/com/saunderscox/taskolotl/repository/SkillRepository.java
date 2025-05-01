package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

  // Find skill by name (exact match)
  Optional<Skill> findByName(String name);

  /**
   * Find skills by name containing with pagination.
   *
   * @param namePart part of the name to search for
   * @param pageable pagination information
   * @return page of matching skills
   */
  Page<Skill> findByNameContainingIgnoreCase(String namePart, Pageable pageable);

  /**
   * Find skills by user with pagination.
   *
   * @param user     the user
   * @param pageable pagination information
   * @return page of skills for the user
   */
  Page<Skill> findByUsersContaining(User user, Pageable pageable);

  // Find skills by board
  List<Skill> findByBoardsContaining(Board board);

  // Find skills by board ID
  @Query("SELECT s FROM Skill s JOIN s.boards b WHERE b.id = :boardId")
  List<Skill> findByBoardId(@Param("boardId") UUID boardId);

  // Find skills by user ID
  @Query("SELECT s FROM Skill s JOIN s.users u WHERE u.id = :userId")
  List<Skill> findByUserId(@Param("userId") UUID userId);

  // Check if skill exists by name (case insensitive)
  boolean existsByNameIgnoreCase(String name);

  // Count skills by user
  long countByUsersContaining(User user);
}