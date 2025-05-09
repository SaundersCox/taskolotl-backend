package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Board entities.
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  /**
   * Finds boards by title containing the given string (case-insensitive).
   *
   * @param title    The title to search for
   * @param pageable Pagination information
   * @return Page of matching boards
   */
  Page<Board> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  /**
   * Finds boards by board type.
   *
   * @param boardType The board type to filter by
   * @param pageable  Pagination information
   * @return Page of matching boards
   */
  Page<Board> findByBoardType(BoardType boardType, Pageable pageable);

  /**
   * Finds boards where the specified user is an owner.
   *
   * @param ownerId  The owner's ID
   * @param pageable Pagination information
   * @return Page of boards owned by the user
   */
  Page<Board> findByOwnersId(UUID ownerId, Pageable pageable);

  /**
   * Finds boards where the specified user is a member.
   *
   * @param memberId The member's ID
   * @param pageable Pagination information
   * @return Page of boards where the user is a member
   */
  Page<Board> findByMembersId(UUID memberId, Pageable pageable);

  /**
   * Finds boards where the specified user is either an owner or a member.
   *
   * @param ownerId  The owner's ID
   * @param memberId The member's ID (typically the same as ownerId)
   * @param pageable Pagination information
   * @return Page of boards accessible to the user
   */
  Page<Board> findByOwnersIdOrMembersId(UUID ownerId, UUID memberId, Pageable pageable);

  /**
   * Finds boards associated with a specific role.
   *
   * @param roleId   The role's ID
   * @param pageable Pagination information
   * @return Page of boards associated with the role
   */
  Page<Board> findByRolesId(UUID roleId, Pageable pageable);

  /**
   * Finds boards associated with a specific skill.
   *
   * @param skillId  The skill's ID
   * @param pageable Pagination information
   * @return Page of boards associated with the skill
   */
  Page<Board> findBySkillsId(UUID skillId, Pageable pageable);

  /**
   * Finds boards that are visible (not private).
   *
   * @param pageable Pagination information
   * @return Page of visible boards
   */
  Page<Board> findByVisibleTrue(Pageable pageable);

  /**
   * Counts the number of boards owned by a specific user.
   *
   * @param ownerId The owner's ID
   * @return The count of boards owned by the user
   */
  long countByOwnersId(UUID ownerId);

  /**
   * Checks if a board exists with the given title (case-insensitive).
   *
   * @param title The title to check
   * @return true if a board with the title exists
   */
  boolean existsByTitleIgnoreCase(String title);
}