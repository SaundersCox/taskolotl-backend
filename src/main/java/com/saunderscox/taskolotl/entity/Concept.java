package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a specific concept within a skill that can be associated with board items.
 */
@Entity
@Table(name = "concepts")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Concept extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 1000)
  private String description;

  @ManyToOne
  @JoinColumn(name = "skill_id")
  private Skill skill;

  @OneToMany(mappedBy = "concept", fetch = FetchType.LAZY)
  @Builder.Default
  private List<BoardItem> boardItems = new ArrayList<>();

  /**
   * Adds a board item to this concept.
   *
   * @param boardItem The board item to associate with this concept
   * @return The associated board item
   */
  public BoardItem addBoardItem(BoardItem boardItem) {
    boardItems.add(boardItem);
    boardItem.setConcept(this);
    return boardItem;
  }

  /**
   * Removes a board item from this concept.
   *
   * @param boardItem The board item to remove
   * @return true if removed, false otherwise
   */
  public boolean removeBoardItem(BoardItem boardItem) {
    boolean removed = boardItems.remove(boardItem);
    if (removed) {
      boardItem.setConcept(null);
    }
    return removed;
  }
}