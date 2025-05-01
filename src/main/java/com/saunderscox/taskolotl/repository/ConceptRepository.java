package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Concept;
import com.saunderscox.taskolotl.entity.Skill;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptRepository extends JpaRepository<Concept, UUID> {

  // Find concepts by skill
  List<Concept> findBySkill(Skill skill);

  // Find concepts by skill ID
  List<Concept> findBySkillId(UUID skillId);

  // Find concept by name and skill
  Optional<Concept> findByNameAndSkill(String name, Skill skill);

  // Find concepts by name containing (case insensitive)
  List<Concept> findByNameContainingIgnoreCase(String namePart);

  // Find concepts by name containing and skill
  List<Concept> findByNameContainingIgnoreCaseAndSkill(String namePart, Skill skill);

  // Find concepts used in board items
  @Query("SELECT DISTINCT c FROM Concept c JOIN c.boardItems bi WHERE bi.board.id = :boardId")
  List<Concept> findConceptsUsedInBoard(@Param("boardId") UUID boardId);

  // Check if concept exists by name and skill
  boolean existsByNameAndSkill(String name, Skill skill);

  // Count concepts by skill
  long countBySkill(Skill skill);
}