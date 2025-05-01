package com.saunderscox.taskolotl.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents a comment on a task. Comments are created by users and can be tagged for
 * categorization and filtering purposes.
 */
@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_comment_board_item_id", columnList = "board_item_id"),
    @Index(name = "idx_comment_author", columnList = "author_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {

  /**
   * Comments are automatically deleted when their author is deleted via database-level cascade.
   */
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User author;

  /**
   * Comments are automatically deleted when their task is deleted via database-level cascade.
   */
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Setter
  private BoardItem boardItem;

  @Column(nullable = false, length = 1000)
  @Setter
  private String description;

  /**
   * Tags are stored in a separate table with lazy loading for performance.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @Column(name = "tag", length = 50)
  @CollectionTable(
      name = "comment_tags",
      indexes = @Index(name = "idx_comment_tags", columnList = "tag")
  )
  @Builder.Default
  private Set<String> tags = new HashSet<>();

  public void setTags(Set<String> tags) {
    this.tags.clear();
    if (tags != null) {
      this.tags.addAll(tags);
    }
  }

  public void addTag(String tag) {
    if (tag != null && !tag.trim().isEmpty()) {
      this.tags.add(tag.trim());
    }
  }

  public void removeTag(String tag) {
    this.tags.remove(tag);
  }
}