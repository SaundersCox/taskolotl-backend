package com.saunderscox.taskolotl.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_oauth_id", columnList = "oauth_id"),
    @Index(name = "idx_user_permission", columnList = "permission")
})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class User extends BaseEntity {

  @Column(nullable = false, length = 50)
  @Setter
  @ToString.Include
  private String username;

  @Column(nullable = false, unique = true, length = 255)
  @Email(message = "Please provide a valid email address")
  @NotBlank(message = "Email is required")
  private String email;

  @Column(length = 100)
  @Setter
  private String oauthProvider;

  @Column(unique = true, length = 100)
  @Setter
  private String oauthId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Permission permission = Permission.USER;

  @Setter
  private String profileDescription;

  @URL
  @Column(length = 255)
  @Setter
  private String profilePictureUrl;

  @ManyToOne
  @JoinColumn(name = "team_id")
  @Setter
  private Team team;

  @OneToMany(mappedBy = "self", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Friendship> friendRequests = new HashSet<>();

  @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Friendship> friendResponses = new HashSet<>();

  @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Comment> comments = new HashSet<>();

  @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
  @Setter
  @Builder.Default
  private Set<Skill> skills = new HashSet<>();

  @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
  @Setter
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @ManyToMany(mappedBy = "owners", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Board> ownedBoards = new HashSet<>();

  @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Board> memberBoards = new HashSet<>();

  @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<BoardItem> boardItems = new HashSet<>();

  @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Study> mentoredStudies = new HashSet<>();

  /**
   * Adds this user as an owner of a board.
   *
   * @param board The board to own
   * @return true if added, false if already an owner
   */
  public boolean addOwnedBoard(Board board) {
    if (ownedBoards.add(board)) {
      board.getOwners().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes this user as an owner of a board.
   *
   * @param board The board to remove ownership from
   * @return true if removed, false if not an owner
   */
  public boolean removeOwnedBoard(Board board) {
    if (ownedBoards.remove(board)) {
      board.getOwners().remove(this);
      return true;
    }
    return false;
  }

  /**
   * Adds this user as a member of a board.
   *
   * @param board The board to join
   * @return true if added, false if already a member
   */
  public boolean addMemberBoard(Board board) {
    if (memberBoards.add(board)) {
      board.getMembers().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes this user as a member of a board.
   *
   * @param board The board to leave
   * @return true if removed, false if not a member
   */
  public boolean removeMemberBoard(Board board) {
    if (memberBoards.remove(board)) {
      board.getMembers().remove(this);
      return true;
    }
    return false;
  }

  /**
   * Adds a role to this user.
   *
   * @param role The role to add
   * @return true if added, false if already has the role
   */
  public boolean addRole(Role role) {
    if (roles.add(role)) {
      role.getUsers().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes a role from this user.
   *
   * @param role The role to remove
   * @return true if removed, false if didn't have the role
   */
  public boolean removeRole(Role role) {
    if (roles.remove(role)) {
      role.getUsers().remove(this);
      return true;
    }
    return false;
  }

  /**
   * Adds a skill to this user.
   *
   * @param skill The skill to add
   * @return true if added, false if already has the skill
   */
  public boolean addSkill(Skill skill) {
    if (skills.add(skill)) {
      skill.getUsers().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes a skill from this user.
   *
   * @param skill The skill to remove
   * @return true if removed, false if didn't have the skill
   */
  public boolean removeSkill(Skill skill) {
    if (skills.remove(skill)) {
      skill.getUsers().remove(this);
      return true;
    }
    return false;
  }

  @PrePersist
  @PreUpdate
  private void prepareEmail() {
    if (email != null) {
      email = email.toLowerCase();
    }
  }
}
