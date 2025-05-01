package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Role} entities. Provides CRUD operations and custom query
 * methods.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

  /**
   * Finds a role by its name.
   *
   * @param name the name of the role
   * @return an Optional containing the role if found, or empty if not found
   */
  Optional<Role> findByName(String name);

  /**
   * Checks if a role with the given name exists.
   *
   * @param name the role name to check
   * @return true if a role with the name exists, false otherwise
   */
  boolean existsByName(String name);
}