package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a professional role within a company or organization. Roles define job positions,
 * responsibilities, and organizational structure.
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

  @NotBlank
  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(length = 100)
  private String hub;

  @Column(length = 100)
  private String team;

  @ManyToMany(fetch = FetchType.LAZY)
  @Builder.Default
  private Set<User> users = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Board> boards = new HashSet<>();

  public boolean addUser(User user) {
    if (users.add(user)) {
      user.getRoles().add(this);
      return true;
    }
    return false;
  }

  public boolean removeUser(User user) {
    if (users.remove(user)) {
      user.getRoles().remove(this);
      return true;
    }
    return false;
  }
}