package com.saunderscox.taskolotl.entity;

/**
 * Represents the current status of a friendship between two users.
 */
public enum FriendshipStatus {
  /**
   * A friendship request has been sent but not yet accepted.
   */
  PENDING,

  /**
   * The friendship request has been accepted and the friendship is active.
   */
  ACCEPTED,

  /**
   * The friendship has been declined by the recipient.
   */
  DECLINED,

  /**
   * The friendship was previously established but has been ended by one user.
   */
  ENDED,

  /**
   * One user has blocked the other, preventing all interactions.
   */
  BLOCKED;
}