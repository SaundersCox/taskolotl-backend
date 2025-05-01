package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardItem;
import com.saunderscox.taskolotl.entity.Concept;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface BoardItemRepository<T extends BoardItem> extends JpaRepository<T, UUID> {

  // Find items by board
  List<T> findByBoard(Board board);

  // Find items by board ordered by position
  List<T> findByBoardOrderByPositionAsc(Board board);

  // Find items by owner
  List<T> findByOwner(User owner);

  // Find items by skill
  List<T> findBySkill(Skill skill);

  // Find items by concept
  List<T> findByConcept(Concept concept);

  // Find items by title containing (case insensitive)
  List<T> findByTitleContainingIgnoreCase(String titlePart);

  // Find items by description containing (case insensitive)
  List<T> findByDescriptionContainingIgnoreCase(String descriptionPart);

  // Combined search by title or description
  List<T> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String titlePart, String descriptionPart);

  // Find items by board with pagination
  Page<T> findByBoard(Board board, Pageable pageable);

  // Find items by title with pagination
  Page<T> findByTitleContainingIgnoreCase(String titlePart, Pageable pageable);

  // Find items by board ID
  List<T> findByBoardId(UUID boardId);

  // Find items by board ID with pagination
  Page<T> findByBoardId(UUID boardId, Pageable pageable);

  // Find items by board ordered by title
  List<T> findByBoardOrderByTitleAsc(Board board);

  // Count items by board
  long countByBoard(Board board);

  // Find max position in a board
  @Query("SELECT MAX(bi.position) FROM #{#entityName} bi WHERE bi.board = :board")
  Integer findMaxPositionInBoard(@Param("board") Board board);
}