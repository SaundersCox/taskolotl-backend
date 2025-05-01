package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Base abstract entity class that provides common fields and functionality for all entities.
 * Includes automatic ID generation and audit timestamps for entity creation and updates.
 */
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
@Getter
@ToString(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

  /**
   * Unique identifier for the entity. Automatically generated as a UUID when the entity is
   * persisted.
   */
  @Id
  @GeneratedValue(generator = "UUID")
  @ToString.Include
  private UUID id;

  /**
   * Version field for optimistic locking.
   */
  @Version
  private Integer version;

  /**
   * Timestamp when the entity was first created. This field is automatically set during entity
   * creation and cannot be modified afterward.
   */
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  /**
   * Timestamp when the entity was last updated. This field is automatically updated whenever the
   * entity is modified.
   */
  @Column(nullable = false)
  private Instant updatedAt;

  /**
   * Lifecycle callback that is automatically executed before a new entity is persisted. Sets both
   * creation and update timestamps to the current time.
   */
  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
    updatedAt = createdAt;
  }

  /**
   * Lifecycle callback that is automatically executed before an existing entity is updated. Updates
   * the update timestamp to the current time.
   */
  @PreUpdate
  public void onUpdate() {
    updatedAt = Instant.now();
  }

  /**
   * Compares entities based on class type and ID. For unpersisted entities (null ID), uses object
   * identity.
   *
   * @param o Object to compare with
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    // Same instance check (handles null ID case and optimization)
    if (this == o) {
      return true;
    }
    // Type check (ensures we're comparing entities of the same type)
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    // Cast to BaseEntity to access ID
    BaseEntity that = (BaseEntity) o;
    // Compare IDs (handles both null and non-null IDs)
    return Objects.equals(getId(), that.getId());
  }

  /**
   * Generates hash code based on entity ID. For unpersisted entities, uses object identity.
   *
   * @return hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}