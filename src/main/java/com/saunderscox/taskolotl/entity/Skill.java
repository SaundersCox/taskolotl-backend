package com.saunderscox.taskolotl.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a skill that can be associated with users, boards, and concepts. Skills provide a way
 * to categorize and organize learning materials and tasks.
 */
@Entity
@Table(name = "skills")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Skill extends BaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  @Setter
  @ToString.Include
  private String name;

  @Column(length = 500)
  @Setter
  private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_skills",
      joinColumns = @JoinColumn(name = "skill_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  @Builder.Default
  private Set<User> users = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "skill_boards",
      joinColumns = @JoinColumn(name = "skill_id"),
      inverseJoinColumns = @JoinColumn(name = "board_id")
  )
  @Builder.Default
  private Set<Board> boards = new HashSet<>();

  @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Concept> concepts = new ArrayList<>();

  /**
   * Adds this skill to a user.
   *
   * @param user The user to add this skill to
   * @return true if added, false if user already has this skill
   */
  public boolean addToUser(User user) {
    if (!users.contains(user)) {
      users.add(user);
      user.getSkills().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes this skill from a user.
   *
   * @param user The user to remove this skill from
   * @return true if removed, false if user didn't have this skill
   */
  public boolean removeFromUser(User user) {
    boolean removed = users.remove(user);
    if (removed) {
      user.getSkills().remove(this);
    }
    return removed;
  }

  /**
   * Adds this skill to a board.
   *
   * @param board The board to add this skill to
   * @return true if added, false if already on the board
   */
  public boolean addToBoard(Board board) {
    if (!boards.contains(board)) {
      boards.add(board);
      board.getSkills().add(this);
      return true;
    }
    return false;
  }

  /**
   * Removes this skill from a board.
   *
   * @param board The board to remove this skill from
   * @return true if removed, false if not on the board
   */
  public boolean removeFromBoard(Board board) {
    boolean removed = boards.remove(board);
    if (removed) {
      board.getSkills().remove(this);
    }
    return removed;
  }

  /**
   * Adds a concept to this skill.
   *
   * @param concept The concept to add
   * @return The added concept
   */
  public Concept addConcept(Concept concept) {
    concepts.add(concept);
    concept.setSkill(this);
    return concept;
  }

  /**
   * Removes a concept from this skill.
   *
   * @param concept The concept to remove
   * @return true if removed, false otherwise
   */
  public boolean removeConcept(Concept concept) {
    boolean removed = concepts.remove(concept);
    if (removed) {
      concept.setSkill(null);
    }
    return removed;
  }
}