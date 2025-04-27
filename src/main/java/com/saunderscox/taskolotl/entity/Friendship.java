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
@Builder
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
public class Friendship extends BaseEntity {

  @ToString.Include
  @ManyToOne(optional = false)
  @JoinColumn(name = "self_id", nullable = false)
  private User self;

  @ToString.Include
  @ManyToOne(optional = false)
  @JoinColumn(name = "target_id", nullable = false)
  private User target;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private FriendshipStatus friendshipStatus = FriendshipStatus.PENDING;

  @Setter
  private boolean isBlocked = false;

  @Column(length = 500)
  private String notes;

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
}