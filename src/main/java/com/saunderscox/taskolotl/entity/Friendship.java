package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Friendship between two users from the perspective of a specific user. The user may block the
 * friend to restrict access to each other's user information and comments.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"self_id", "target_id"}),}, indexes = {
    @Index(name = "idx_friendship_self", columnList = "self_id"),
    @Index(name = "idx_friendship_target", columnList = "target_id")})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
public class Friendship extends BaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "self_id", nullable = false)
  @ToString.Include
  private User self;

  @ManyToOne(optional = false)
  @JoinColumn(name = "target_id", nullable = false)
  @ToString.Include
  private User target;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  @Setter
  private FriendshipStatus friendshipStatus = FriendshipStatus.PENDING;

  @Builder.Default
  @Setter
  private boolean blocked = false;

  @Column(length = 500)
  @Setter
  private String selfNotes;

  @Column(length = 500)
  @Setter
  private String friendNotes;

  /**
   * Accepts a pending friendship request.
   */
  public void accept() {
    if (friendshipStatus == FriendshipStatus.PENDING) {
      friendshipStatus = FriendshipStatus.ACCEPTED;
    }
  }

  /**
   * Decline a pending friendship request.
   */
  public void decline() {
    if (friendshipStatus == FriendshipStatus.PENDING) {
      friendshipStatus = FriendshipStatus.DECLINED;
    }
  }

  /**
   * Determines if this status allows for interaction between users.
   *
   * @return true if users can interact, false otherwise
   */
  public boolean allowsInteraction() {
    return friendshipStatus == FriendshipStatus.ACCEPTED;
  }

  /**
   * Determines if this status can be changed to ACCEPTED.
   *
   * @return true if the status can be accepted, false otherwise
   */
  public boolean canBeAccepted() {
    return friendshipStatus == FriendshipStatus.PENDING;
  }

  /**
   * Adds notes to the friendship from the perspective of the given user. Updates either selfNotes
   * or friendNotes depending on which user is adding the note.
   *
   * @param user  The user adding the note
   * @param notes The notes to add
   * @return true if notes were added, false if the user is not part of this friendship
   */
  public boolean addNotes(User user, String notes) {
    if (user.equals(self)) {
      this.selfNotes = notes;
      return true;
    } else if (user.equals(target)) {
      this.friendNotes = notes;
      return true;
    }
    return false;
  }
}