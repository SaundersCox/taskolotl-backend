package com.saunderscox.taskolotl.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

@Entity
@Table(name = "board_items", indexes = {
    @Index(name = "idx_board_item_board", columnList = "board_id"),
    @Index(name = "idx_board_item_owner", columnList = "owner_id"),
    @Index(name = "idx_board_item_skill", columnList = "skill_id"),
    @Index(name = "idx_board_item_concept", columnList = "concept_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class BoardItem extends BaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id")
  private User owner;

  @NotBlank
  @Size(min = 1, max = 100)
  @Column(nullable = false, length = 100)
  @Setter
  @ToString.Include
  private String title;

  @Size(max = 1000)
  @Column(length = 1000)
  @Setter
  private String description;

  @Column(nullable = false)
  @Setter
  @Builder.Default
  private Integer position = 0;

  /**
   * Hex color code for the item (e.g., "#FF5733"). Format: # followed by 6 hexadecimal characters.
   */
  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code (e.g., #FF5733)")
  @Column(name = "color", length = 7)
  @Setter
  private String color;

  @OneToMany(mappedBy = "boardItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Comment> comments = new ArrayList<>();

  @Setter
  @ManyToOne(optional = false)
  @JoinColumn(name = "board_id")
  private Board board;

  @Setter
  @ManyToOne
  @JoinColumn(name = "skill_id")
  private Skill skill;

  @Setter
  @ManyToOne
  @JoinColumn(name = "concept_id")
  private Concept concept;

  /**
   * Adds a comment to this board item.
   *
   * @param comment The comment to add
   * @return The added comment
   */
  public Comment addComment(Comment comment) {
    comments.add(comment);
    comment.setBoardItem(this);
    return comment;
  }

  /**
   * Removes a comment from this board item.
   *
   * @param comment The comment to remove
   * @return true if the comment was removed, false otherwise
   */
  public boolean removeComment(Comment comment) {
    boolean removed = comments.remove(comment);
    if (removed) {
      comment.setBoardItem(null);
    }
    return removed;
  }

  /**
   * Sets both skill and concept together, ensuring they are compatible. The concept must belong to
   * the specified skill.
   *
   * @param skill   The skill to set
   * @param concept The concept to set
   * @throws IllegalArgumentException if the concept doesn't belong to the skill
   */
  public void setSkillAndConcept(Skill skill, Concept concept) {
    if (concept != null && skill != null && !concept.getSkill().equals(skill)) {
      throw new IllegalArgumentException("Concept must belong to the specified skill");
    }

    this.skill = skill;
    this.concept = concept;
  }

  /**
   * Normalizes the color format before saving. Ensures the hex code is uppercase and has a #
   * prefix.
   */
  @PrePersist
  @PreUpdate
  protected void normalizeColor() {
    if (color != null) {
      // Add # if missing
      if (!color.startsWith("#")) {
        color = "#" + color;
      }

      // Convert to uppercase
      color = color.toUpperCase();
    }
  }
}