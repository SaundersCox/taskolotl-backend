package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardType;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  // Find boards by owner
  List<Board> findByOwnersContaining(User owner);

  // Find boards by member
  List<Board> findByMembersContaining(User member);

  // Find boards by type
  List<Board> findByBoardType(BoardType boardType);

  /**
   * Find boards by type with pagination.
   *
   * @param boardType the board type
   * @param pageable  pagination information
   * @return page of matching boards
   */
  Page<Board> findByBoardType(BoardType boardType, Pageable pageable);

  // Find boards by title containing (case insensitive)
  List<Board> findByTitleContainingIgnoreCase(String titlePart);

  /**
   * Find boards by title containing with pagination.
   *
   * @param titlePart part of the title to search for
   * @param pageable  pagination information
   * @return page of matching boards
   */
  Page<Board> findByTitleContainingIgnoreCase(String titlePart, Pageable pageable);


  // Find public boards
  List<Board> findByVisible(boolean visible);

  // Find boards by owner or member with pagination
  @Query("SELECT b FROM Board b WHERE :user MEMBER OF b.owners OR :user MEMBER OF b.members")
  Page<Board> findByUserWithAccess(@Param("user") User user, Pageable pageable);

  // Find boards by skill
  @Query("SELECT b FROM Board b JOIN b.skills s WHERE s.id = :skillId")
  List<Board> findBySkillId(@Param("skillId") UUID skillId);

  // Find boards by role
  @Query("SELECT b FROM Board b JOIN b.roles r WHERE r.id = :roleId")
  List<Board> findByRoleId(@Param("roleId") UUID roleId);

  // Count boards by owner
  long countByOwnersContaining(User owner);
}