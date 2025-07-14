package com.saunderscox.taskolotl.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

/**
 * Represents a board that contains either tasks or studies and has associated users, roles, and
 * skills. A board serves as the primary organizational unit.
 */
@Entity
@Table(name = "boards", indexes = {@Index(name = "idx_board_title", columnList = "title")})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
public class Board extends BaseEntity {

  @Column(nullable = false, length = 100)
  @NotBlank
  @Setter
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BoardType boardType;

  @Column(length = 500)
  @Setter
  private String description;

  @Setter
  private boolean visible;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "board_owners", joinColumns = @JoinColumn(name = "board_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  @Builder.Default
  private Set<User> owners = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "board_members", joinColumns = @JoinColumn(name = "board_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  @Builder.Default
  private Set<User> members = new LinkedHashSet<>();

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

  protected boolean addBoardItem(BoardItem boardItem) {
    return boardItems.add(boardItem);
  }

  protected boolean removeBoardItem(BoardItem boardItem) {
    return boardItems.remove(boardItem);
  }

  public boolean isOwner(User user) {
    return owners.contains(user);
  }

  public boolean isMember(User user) {
    return members.contains(user);
  }

  public boolean hasAccess(User user) {
    return isOwner(user) || isMember(user);
  }

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

  public boolean addOwner(User user) {
    if (owners.add(user)) {
      user.getOwnedBoards().add(this);
      return true;
    }
    return false;
  }

  public boolean removeOwner(User user) {
    if (owners.remove(user)) {
      user.getOwnedBoards().remove(this);
      return true;
    }
    return false;
  }

  public boolean addMember(User user) {
    if (members.add(user)) {
      user.getMemberBoards().add(this);
      return true;
    }
    return false;
  }

  public boolean removeMember(User user) {
    if (members.remove(user)) {
      user.getMemberBoards().remove(this);
      return true;
    }
    return false;
  }

  public boolean addRole(Role role) {
    if (roles.add(role)) {
      role.getBoards().add(this);
      return true;
    }
    return false;
  }

  public boolean removeRole(Role role) {
    if (roles.remove(role)) {
      role.getBoards().remove(this);
      return true;
    }
    return false;
  }

  public boolean addSkill(Skill skill) {
    if (skills.add(skill)) {
      skill.getBoards().add(this);
      return true;
    }
    return false;
  }

  public boolean removeSkill(Skill skill) {
    if (skills.remove(skill)) {
      skill.getBoards().remove(this);
      return true;
    }
    return false;
  }
}
