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

  public boolean isCurrentUser(UUID userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UUID currentUserId = UUID.fromString(authentication.getName());
    return currentUserId.equals(userId);
  }

  public Page<UserResponse> getAllUsers(Pageable pageable) {
    log.debug("Fetching all users");
    return userRepository.findAll(pageable)
        .map(userMapper::toResponseDto);
  }

  public UserResponse getUserById(UUID id) {
    log.debug("Fetching user with ID: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
    return userMapper.toResponseDto(user);
  }

  public UserResponse getUserByEmail(String email) {
    log.debug("Fetching user with email: {}", email);
    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return userMapper.toResponseDto(user);
  }

  public UserResponse createUser(UserCreateRequest dto) {
    log.info("Creating new user with username: {}", dto.getUsername());

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

  public UserResponse updateUser(UUID id, UserUpdateRequest dto) {
    log.info("Updating user with ID: {}", id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

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

  public void deleteUser(UUID id) {
    log.info("Deleting user with ID: {}", id);

    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id);
    }

    userRepository.deleteById(id);
    log.info("User deleted successfully");
  }

  public Page<UserResponse> searchUsers(String query, Pageable pageable) {
    log.debug("Searching users with query: {} and pagination: page={}, size={}",
        query, pageable.getPageNumber(), pageable.getPageSize());

    return userRepository
        .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
        .map(userMapper::toResponseDto);
  }

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

  public UserResponse removeSkillFromUser(UUID userId, UUID skillId) {
    log.info("Removing skill {} from user {}", skillId, userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    skillRepository.findById(skillId).ifPresent(user::removeSkill);
    User updatedUser = userRepository.save(user);
    log.info("Skill removed successfully from user");
    return userMapper.toResponseDto(updatedUser);
  }

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

  public UserResponse removeRoleFromUser(UUID userId, UUID roleId) {
    log.info("Removing role {} from user {}", roleId, userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    roleRepository.findById(roleId).ifPresent(user::removeRole);
    User updatedUser = userRepository.save(user);
    log.info("Role removed successfully from user");
    return userMapper.toResponseDto(updatedUser);
  }

  public UserResponse getUserByOauthId(String oauthId) {
    log.debug("Fetching user with OAuth2 ID: {}", oauthId);
    User user = userRepository.findByOauthId(oauthId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + oauthId));
    return userMapper.toResponseDto(user);
  }

  public UserResponse getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = UUID.fromString(authentication.getName()); // JWT subject is User UUID
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    return userMapper.toResponseDto(user);

  }

  public boolean isUserIdMatchingEmail(UUID userId, String email) {
    return userRepository.findById(userId)
        .map(user -> user.getEmail().equalsIgnoreCase(email))
        .orElse(false);
  }

  public User findOrCreateOAuth2User(String oauthId, String email, String name, String provider) {
    log.info("Finding or creating OAuth2 user: {} from provider: {}", email, provider);

    // User with OAuth ID exists
    try {
      UserResponse existingUser = getUserByOauthId(oauthId);
      return userRepository.findById(existingUser.getId())
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    } catch (ResourceNotFoundException ignored) { // Continue
    }

    // Link OAuth ID to user with email (no current email-only flow)
    try {
      UserResponse existingUser = getUserByEmail(email);
      User user = userRepository.findById(existingUser.getId())
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));

      user.setOauthId(oauthId);
      user.setOauthProvider(provider);
      log.info("Linked OAuth ID to existing user: {}", email);
      return userRepository.save(user);
    } catch (ResourceNotFoundException ignored) { // Continue
    }

    // Create new user
    UserCreateRequest createRequest = new UserCreateRequest();
    createRequest.setEmail(email);
    createRequest.setUsername(name);

    UserResponse newUserResponse = createUser(createRequest);
    User newUser = userRepository.findById(newUserResponse.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Failed to create user"));

    newUser.setOauthId(oauthId);
    newUser.setOauthProvider(provider);

    User savedUser = userRepository.save(newUser);
    log.info("Created new OAuth2 user: {} with ID: {}", email, savedUser.getId());
    return savedUser;
  }

}
