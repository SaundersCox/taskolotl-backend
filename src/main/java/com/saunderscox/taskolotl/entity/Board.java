package com.saunderscox.taskolotl.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents a board that contains either tasks or studies and has associated users, roles, and
 * skills. A board serves as the primary organizational unit.
 */
@Entity
@Table(name = "board", indexes = {@Index(name = "idx_board_title", columnList = "title")})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Changed from @Builder to @SuperBuilder for inheritance compatibility
@Getter
@EqualsAndHashCode(callSuper = true)
public class Board extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BoardType boardType;

  @Column(length = 500)
  private String description;

  /**
   * When true, the board is only visible to owners and members.
   */
  private boolean isPrivate;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "board_owners", joinColumns = @JoinColumn(name = "board_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  @Builder.Default
  private Set<User> owners = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "board_members", joinColumns = @JoinColumn(name = "board_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  @Builder.Default
  private Set<User> members = new HashSet<>();

  /**
   * Items are ordered by their position field. They are fully owned by the board and will be
   * deleted if the board is deleted.
   */
  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  @Builder.Default
  private List<BoardItem> boardItems = new ArrayList<>();

  @ManyToMany(mappedBy = "boards", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @ManyToMany(mappedBy = "boards", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Skill> skills = new HashSet<>();

  /**
   * Adds an item to this board. Updates both sides of the bidirectional relationship.
   *
   * @param boardItem The item to add to this board
   * @return The added item
   */
  public BoardItem addBoardItem(BoardItem boardItem) {
    boardItems.add(boardItem);
    boardItem.setBoard(this);
    return boardItem;
  }

  /**
   * Removes an item from this board. Updates both sides of the bidirectional relationship.
   *
   * @param boardItem The item to remove
   * @return true if the item was removed, false otherwise
   */
  public boolean removeBoardItem(BoardItem boardItem) {
    boolean removed = boardItems.remove(boardItem);
    if (removed) {
      boardItem.setBoard(null);
    }
    return removed;
  }

  /**
   * Checks if a user is an owner of this board.
   *
   * @param user The user to check
   * @return true if the user is an owner, false otherwise
   */
  public boolean isOwner(User user) {
    return owners.contains(user);
  }

  /**
   * Checks if a user is a member of this board.
   *
   * @param user The user to check
   * @return true if the user is a member, false otherwise
   */
  public boolean isMember(User user) {
    return members.contains(user);
  }

  /**
   * Checks if a user has access to this board (is either an owner or member).
   *
   * @param user The user to check
   * @return true if the user has access, false otherwise
   */
  public boolean hasAccess(User user) {
    return isOwner(user) || isMember(user);
  }

  /**
   * Moves a board item to a new position in the order sequence.
   *
   * @param item        The item to move
   * @param newPosition The new position for the item
   * @throws IllegalArgumentException if the item doesn't belong to this board
   */
  public void moveItemToPosition(BoardItem item, int newPosition) {
    // Validate item belongs to this board
    if (!boardItems.contains(item)) {
      throw new IllegalArgumentException("Item does not belong to this board");
    }

    // Get current position
    int currentPosition = boardItems.indexOf(item);

    // Update positions of affected items
    if (newPosition < currentPosition) {
      // Moving up - shift items down
      boardItems.stream()
          .filter(i -> i.getPosition() >= newPosition && i.getPosition() < currentPosition)
          .forEach(i -> i.setPosition(i.getPosition() + 1));
    } else if (newPosition > currentPosition) {
      // Moving down - shift items up
      boardItems.stream()
          .filter(i -> i.getPosition() <= newPosition && i.getPosition() > currentPosition)
          .forEach(i -> i.setPosition(i.getPosition() - 1));
    }

    // Set new position
    item.setPosition(newPosition);
  }

  /**
   * Adds an owner to this board.
   *
   * @param user The user to add as owner
   * @return true if added, false if already an owner
   */
  public boolean addOwner(User user) {
    if (owners.add(user)) {
      user.getOwnedBoards().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes an owner from this board.
   *
   * @param user The user to remove as owner
   * @return true if removed, false if not an owner
   */
  public boolean removeOwner(User user) {
    if (owners.remove(user)) {
      user.getOwnedBoards().remove(this);
      return true;
    }
    return false;
  }

  /**
   * Adds a member to this board.
   *
   * @param user The user to add as member
   * @return true if added, false if already a member
   */
  public boolean addMember(User user) {
    if (members.add(user)) {
      user.getMemberBoards().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes a member from this board.
   *
   * @param user The user to remove as member
   * @return true if removed, false if not a member
   */
  public boolean removeMember(User user) {
    if (members.remove(user)) {
      user.getMemberBoards().remove(this);
      return true;
    }
    return false;
  }

  /**
   * Adds a role to this board.
   *
   * @param role The role to add
   * @return true if added, false if already has the role
   */
  public boolean addRole(Role role) {
    if (roles.add(role)) {
      role.getBoards().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes a role from this board.
   *
   * @param role The role to remove
   * @return true if removed, false if didn't have the role
   */
  public boolean removeRole(Role role) {
    if (roles.remove(role)) {
      role.getBoards().remove(this);
      return true;
    }
    return false;
  }

  /**
   * Adds a skill to this board.
   *
   * @param skill The skill to add
   * @return true if added, false if already has the skill
   */
  public boolean addSkill(Skill skill) {
    if (skills.add(skill)) {
      skill.getBoards().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes a skill from this board.
   *
   * @param skill The skill to remove
   * @return true if removed, false if didn't have the skill
   */
  public boolean removeSkill(Skill skill) {
    if (skills.remove(skill)) {
      skill.getBoards().remove(this);
      return true;
    }
    return false;
  }
}