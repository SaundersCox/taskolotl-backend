package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.BoardItem;
import com.saunderscox.taskolotl.entity.Comment;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Comment} entities.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  /**
   * Finds all comments for a specific board item.
   *
   * @param boardItem the board item
   * @return list of comments for the board item
   */
  List<Comment> findByBoardItem(BoardItem boardItem);

  /**
   * Finds all comments for a specific board item ID.
   *
   * @param boardItemId the board item ID
   * @return list of comments for the board item
   */
  List<Comment> findByBoardItemId(UUID boardItemId);

  /**
   * Finds all comments created by a specific user.
   *
   * @param author the author
   * @return list of comments by the author
   */
  List<Comment> findByAuthor(User author);

  /**
   * Finds all comments created by a specific user ID.
   *
   * @param authorId the author ID
   * @return list of comments by the author
   */
  List<Comment> findByAuthorId(UUID authorId);

  /**
   * Finds all comments containing a specific tag.
   *
   * @param tag the tag to search for
   * @return list of comments with the tag
   */
  List<Comment> findByTagsContaining(String tag);
}