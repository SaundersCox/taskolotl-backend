package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.dto.UserCreateRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.dto.UserUpdateRequest;
import com.saunderscox.taskolotl.entity.Role;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.exception.ResourceNotFoundException;
import com.saunderscox.taskolotl.mapper.UserMapper;
import com.saunderscox.taskolotl.repository.RoleRepository;
import com.saunderscox.taskolotl.repository.SkillRepository;
import com.saunderscox.taskolotl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

/**
 * Service for managing user operations including CRUD operations and user-related business logic.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
  private final UserRepository userRepository;
  private final SkillRepository skillRepository;
  private final RoleRepository roleRepository;
  private final UserMapper userMapper;

  /**
   * Checks if the current authenticated user matches the given user ID
   *
   * @param userId The user ID to check
   * @return true if the current user matches the ID
   */
  public boolean isCurrentUser(UUID userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    return isUserIdMatchingEmail(userId, email);
  }

  /**
   * Retrieves all users from the database
   *
   * @return List of all users as DTOs
   */
  public Page<UserResponse> getAllUsers(Pageable pageable) {
    log.debug("Fetching all users");
    return userRepository.findAll(pageable)
        .map(userMapper::toResponseDto);
  }

  /**
   * Retrieves a user by their ID
   *
   * @param id The user ID
   * @return The user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public UserResponse getUserById(UUID id) {
    log.debug("Fetching user with ID: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
    return userMapper.toResponseDto(user);
  }

  /**
   * Retrieves a user by their email
   *
   * @param email The user's email
   * @return The user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public UserResponse getUserByEmail(String email) {
    log.debug("Fetching user with email: {}", email);
    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return userMapper.toResponseDto(user);
  }

  /**
   * Creates a new user
   *
   * @param dto The user creation request
   * @return The created user as a DTO
   * @throws IllegalArgumentException if email or username already exists
   */
  public UserResponse createUser(UserCreateRequest dto) {
    log.info("Creating new user with username: {}", dto.getUsername());

    // Check if email or username already exists
    if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
      log.warn("Email already in use: {}", dto.getEmail());
      throw new IllegalArgumentException("Email already in use");
    }

    if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
      log.warn("Username already in use: {}", dto.getUsername());
      throw new IllegalArgumentException("Username already in use");
    }

    User user = userMapper.toEntity(dto);
    user.setSkills(new HashSet<>());
    user.setRoles(new HashSet<>());

    User savedUser = userRepository.save(user);
    log.info("User created successfully with ID: {}", savedUser.getId());
    return userMapper.toResponseDto(savedUser);
  }

  /**
   * Updates an existing user
   *
   * @param id  The user ID
   * @param dto The update request
   * @return The updated user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   * @throws IllegalArgumentException  if username is already taken by another user
   */
  public UserResponse updateUser(UUID id, UserUpdateRequest dto) {
    log.info("Updating user with ID: {}", id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

    // Check if username is already taken by another user
    if (dto.getUsername() != null) {
      userRepository.findByUsernameIgnoreCase(dto.getUsername())
          .ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
              log.warn("Username already in use by another user: {}", dto.getUsername());
              throw new IllegalArgumentException("Username already in use");
            }
          });
    }

    userMapper.updateEntityFromDto(dto, user);
    User updatedUser = userRepository.save(user);
    log.info("User updated successfully: {}", updatedUser.getUsername());
    return userMapper.toResponseDto(updatedUser);
  }

  /**
   * Deletes a user
   *
   * @param id The user ID
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public void deleteUser(UUID id) {
    log.info("Deleting user with ID: {}", id);

    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id);
    }

    userRepository.deleteById(id);
    log.info("User deleted successfully");
  }

  /**
   * Searches for users by username or email
   *
   * @param query The search query
   * @return List of matching users as DTOs
   */
  public Page<UserResponse> searchUsers(String query, Pageable pageable) {
    log.debug("Searching users with query: {} and pagination: page={}, size={}",
        query, pageable.getPageNumber(), pageable.getPageSize());

    return userRepository
        .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
        .map(userMapper::toResponseDto);
  }

  /**
   * Adds a skill to a user
   *
   * @param userId  The user ID
   * @param skillId The skill ID
   * @return The updated user as a DTO
   * @throws ResourceNotFoundException if the user or skill doesn't exist
   */
  public UserResponse addSkillToUser(UUID userId, UUID skillId) {
    log.info("Adding skill {} to user {}", skillId, userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    Skill skill = skillRepository.findById(skillId)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

    user.addSkill(skill);
    User updatedUser = userRepository.save(user);
    log.info("Skill added successfully to user");
    return userMapper.toResponseDto(updatedUser);
  }

  /**
   * Removes a skill from a user
   *
   * @param userId  The user ID
   * @param skillId The skill ID
   * @return The updated user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public UserResponse removeSkillFromUser(UUID userId, UUID skillId) {
    log.info("Removing skill {} from user {}", skillId, userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    skillRepository.findById(skillId).ifPresent(user::removeSkill);
    User updatedUser = userRepository.save(user);
    log.info("Skill removed successfully from user");
    return userMapper.toResponseDto(updatedUser);
  }

  /**
   * Adds a role to a user
   *
   * @param userId The user ID
   * @param roleId The role ID
   * @return The updated user as a DTO
   * @throws ResourceNotFoundException if the user or role doesn't exist
   */
  public UserResponse addRoleToUser(UUID userId, UUID roleId) {
    log.info("Adding role {} to user {}", roleId, userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    Role role = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

    user.addRole(role);
    User updatedUser = userRepository.save(user);
    log.info("Role added successfully to user");
    return userMapper.toResponseDto(updatedUser);
  }

  /**
   * Removes a role from a user
   *
   * @param userId The user ID
   * @param roleId The role ID
   * @return The updated user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public UserResponse removeRoleFromUser(UUID userId, UUID roleId) {
    log.info("Removing role {} from user {}", roleId, userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    roleRepository.findById(roleId).ifPresent(user::removeRole);
    User updatedUser = userRepository.save(user);
    log.info("Role removed successfully from user");
    return userMapper.toResponseDto(updatedUser);
  }

  /**
   * Retrieves a user by their OAuth2 ID
   *
   * @param oauthId The user OAuth2 ID
   * @return The user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public UserResponse getUserByOauthId(String oauthId) {
    log.debug("Fetching user with OAuth2 ID: {}", oauthId);
    User user = userRepository.findByOauthId(oauthId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + oauthId));
    return userMapper.toResponseDto(user);
  }

  /**
   * Gets the current authenticated user
   *
   * @return The current user as a DTO
   * @throws ResourceNotFoundException if the user doesn't exist
   */
  public UserResponse getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String oauthId = authentication.getName();
    return getUserByOauthId(oauthId);
  }


  /**
   * Checks if a user ID matches a user with the given email Used for authorization checks
   *
   * @param userId The user ID to check
   * @param email  The email to check against
   * @return true if the user ID belongs to the user with the given email
   */
  public boolean isUserIdMatchingEmail(UUID userId, String email) {
    return userRepository.findById(userId)
        .map(user -> user.getEmail().equalsIgnoreCase(email))
        .orElse(false);
  }
}
