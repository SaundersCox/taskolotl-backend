package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

/**
 * Represents a team within an organization. Teams group users together and can be associated with a
 * hub.
 */
@Entity
@Table(name = "teams", indexes = {
    @Index(name = "idx_team_name", columnList = "name")
})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
public class Team extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @ManyToOne(optional = false)
  @JoinColumn(name = "hub_id")
  @Setter
  private Hub hub;

  @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<User> users = new HashSet<>();

  /**
   * Adds a user to the team.
   *
   * @param user The user to add.
   * @return true if added, false if already in team
   */
  public boolean addUser(User user) {
    if (users.contains(user)) {
      return false;
    }
    if (user.getTeam() != null && user.getTeam() != this) {
      user.getTeam().getUsers().remove(user);
    }
    users.add(user);
    user.setTeam(this);
    return true;
  }
}