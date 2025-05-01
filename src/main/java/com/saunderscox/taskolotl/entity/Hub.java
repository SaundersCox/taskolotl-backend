package com.saunderscox.taskolotl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents the enterprise level hub within a company or organization. Hubs define teams and
 * domains.
 */
@Entity
@Table(name = "hubs", indexes = {
    @Index(name = "idx_hub_name", columnList = "name")
})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
public class Hub extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @OneToMany(mappedBy = "hub", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Team> teams = new HashSet<>();

  /**
   * Adds a team to this hub. If the team is already in another hub, it is removed from that hub
   * first.
   *
   * @param team The team to add
   * @return true if added, false if the team was already in this hub
   */
  public boolean addTeam(Team team) {
    if (teams.contains(team)) {
      return false;
    }
    if (team.getHub() != null && team.getHub() != this) {
      team.getHub().getTeams().remove(team);
    }
    teams.add(team);
    team.setHub(this);
    return true;
  }
}