package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Friendship;
import com.saunderscox.taskolotl.entity.FriendshipStatus;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Friendship} entities.
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

  /**
   * Finds a friendship between two users.
   *
   * @param self   the self user
   * @param target the target user
   * @return the friendship if it exists
   */
  Optional<Friendship> findBySelfAndTarget(User self, User target);

  /**
   * Finds a friendship between two users by their IDs.
   *
   * @param selfId   the self user ID
   * @param targetId the target user ID
   * @return the friendship if it exists
   */
  Optional<Friendship> findBySelfIdAndTargetId(UUID selfId, UUID targetId);

  /**
   * Finds all friendships where the user is either self or target.
   *
   * @param user the user
   * @return list of friendships involving the user
   */
  List<Friendship> findBySelfOrTarget(User user, User sameUser);

  /**
   * Finds all friendships where the user is self.
   *
   * @param self the self user
   * @return list of friendships where user is self
   */
  List<Friendship> findBySelf(User self);

  /**
   * Finds all friendships where the user is target.
   *
   * @param target the target user
   * @return list of friendships where user is target
   */
  List<Friendship> findByTarget(User target);

  /**
   * Finds all friendships with a specific status.
   *
   * @param status the friendship status
   * @return list of friendships with the status
   */
  List<Friendship> findByFriendshipStatus(FriendshipStatus status);

  /**
   * Finds all blocked friendships for a user.
   *
   * @param self    the user
   * @param blocked the blocked status
   * @return list of blocked friendships
   */
  List<Friendship> findBySelfAndBlocked(User self, boolean blocked);
}