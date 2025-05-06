package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Find a user by their email address (case insensitive)
   *
   * @param email The email to search for
   * @return An Optional containing the user if found
   */
  Optional<User> findByEmailIgnoreCase(String email);

  /**
   * Find a user by their username (case insensitive)
   *
   * @param username The username to search for
   * @return An Optional containing the user if found
   */
  Optional<User> findByUsernameIgnoreCase(String username);

  /**
   * Find a user by their OAuth ID
   *
   * @param oauthId The OAuth ID to search for
   * @return An Optional containing the user if found
   */
  Optional<User> findByOauthId(String oauthId);

  /**
   * Check if a user exists with the given email
   *
   * @param email The email to check
   * @return true if a user exists with this email
   */
  boolean existsByEmailIgnoreCase(String email);

  /**
   * Check if a user exists with the given username
   *
   * @param username The username to check
   * @return true if a user exists with this username
   */
  boolean existsByUsernameIgnoreCase(String username);


  /**
   * Search for users by username or email (case insensitive)
   *
   * @param query    The search term
   * @param pageable Pageable object for pagination
   * @return A Page of matching users
   */
  Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String query,
      Pageable pageable);
}