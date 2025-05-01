package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.dto.UserCreateRequestDto;
import com.saunderscox.taskolotl.dto.UserResponseDto;
import com.saunderscox.taskolotl.dto.UserUpdateRequestDto;
import com.saunderscox.taskolotl.entity.Role;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.exception.DuplicateResourceException;
import com.saunderscox.taskolotl.exception.ResourceNotFoundException;
import com.saunderscox.taskolotl.mapper.UserMapper;
import com.saunderscox.taskolotl.repository.RoleRepository;
import com.saunderscox.taskolotl.repository.SkillRepository;
import com.saunderscox.taskolotl.repository.UserRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing users.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final SkillRepository skillRepository;
  private final RoleRepository roleRepository;
  private final UserMapper userMapper;
  private final JwtService jwtService;

  /**
   * Creates a new user.
   *
   * @param dto the user creation request
   * @return the created user
   * @throws DuplicateResourceException if a user with the same username or email already exists
   */
  public UserResponseDto createUser(UserCreateRequestDto dto) {
    if (userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
      throw new DuplicateResourceException("User", "username", dto.getUsername());
    }
    if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
      throw new DuplicateResourceException("User", "email", dto.getEmail());
    }

    User user = userMapper.toEntity(dto);
    user = userRepository.save(user);
    return userMapper.toResponseDto(user);
  }

  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsernameIgnoreCase(username);
  }

  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmailIgnoreCase(email);
  }

  /**
   * Gets a user by ID.
   *
   * @param id the user ID
   * @return the user
   * @throws ResourceNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserResponseDto getUserById(UUID id) {
    User user = findUserById(id);
    return userMapper.toResponseDto(user);
  }

  /**
   * Gets a user by username.
   *
   * @param username the username
   * @return the user
   * @throws ResourceNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserResponseDto getUserByUsername(String username) {
    User user = userRepository.findByUsernameIgnoreCase(username)
        .orElseThrow(
            () -> new ResourceNotFoundException("User not found with username: " + username));
    return userMapper.toResponseDto(user);
  }

  /**
   * Gets a user by email.
   *
   * @param email the email
   * @return the user
   * @throws ResourceNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserResponseDto getUserByEmail(String email) {
    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return userMapper.toResponseDto(user);
  }

  /**
   * Gets a user by OAuth ID.
   *
   * @param oauthId the OAuth ID
   * @return the user
   * @throws ResourceNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserResponseDto getUserByOauthId(String oauthId) {
    User user = userRepository.findByOauthId(oauthId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User not found with OAuth ID: " + oauthId));
    return userMapper.toResponseDto(user);
  }

  /**
   * Gets all users with pagination.
   *
   * @param pageable pagination information
   * @return page of users
   */
  @Transactional(readOnly = true)
  public Page<UserResponseDto> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(userMapper::toResponseDto);
  }

  /**
   * Updates a user.
   *
   * @param id  the user ID
   * @param dto the user update request
   * @return the updated user
   * @throws ResourceNotFoundException  if the user is not found
   * @throws DuplicateResourceException if the new username already exists
   */
  public UserResponseDto updateUser(UUID id, UserUpdateRequestDto dto) {
    User user = findUserById(id);

    if (dto.getUsername() != null && !dto.getUsername().equalsIgnoreCase(user.getUsername()) &&
        userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
      throw new DuplicateResourceException("User", "username", dto.getUsername());
    }

    userMapper.updateEntityFromDto(dto, user);
    user = userRepository.save(user);
    return userMapper.toResponseDto(user);
  }

  /**
   * Deletes a user.
   *
   * @param id the user ID
   * @throws ResourceNotFoundException if the user is not found
   */
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
  }

  /**
   * Adds skills to a user.
   *
   * @param userId   the user ID
   * @param skillIds the skill IDs to add
   * @return the updated user
   * @throws ResourceNotFoundException if the user or any skill is not found
   */
  public UserResponseDto addSkillsToUser(UUID userId, Set<UUID> skillIds) {
    User user = findUserById(userId);
    Set<Skill> skillsToAdd = new HashSet<>(skillRepository.findAllById(skillIds));
    skillsToAdd.forEach(user::addSkill);
    User savedUser = userRepository.save(user);
    return userMapper.toResponseDto(savedUser);
  }

  /**
   * Removes skills from a user.
   *
   * @param userId   the user ID
   * @param skillIds the skill IDs to remove
   * @return the updated user
   * @throws ResourceNotFoundException if the user is not found
   */
  public UserResponseDto removeSkillsFromUser(UUID userId, Set<UUID> skillIds) {
    User user = findUserById(userId);
    Set<Skill> skillsToRemove = new HashSet<>(skillRepository.findAllById(skillIds));
    skillsToRemove.forEach(user::removeSkill);
    User savedUser = userRepository.save(user);
    return userMapper.toResponseDto(savedUser);
  }

  /**
   * Adds roles to a user.
   *
   * @param userId  the user ID
   * @param roleIds the role IDs to add
   * @return the updated user
   * @throws ResourceNotFoundException if the user or any role is not found
   */
  public UserResponseDto addRolesToUser(UUID userId, Set<UUID> roleIds) {
    User user = findUserById(userId);
    Set<Role> rolesToAdd = new HashSet<>(roleRepository.findAllById(roleIds));
    rolesToAdd.forEach(user::addRole);
    User savedUser = userRepository.save(user);
    return userMapper.toResponseDto(savedUser);
  }

  /**
   * Removes roles from a user.
   *
   * @param userId  the user ID
   * @param roleIds the role IDs to remove
   * @return the updated user
   * @throws ResourceNotFoundException if the user is not found
   */
  public UserResponseDto removeRolesFromUser(UUID userId, Set<UUID> roleIds) {
    User user = findUserById(userId);
    Set<Role> rolesToRemove = new HashSet<>(roleRepository.findAllById(roleIds));
    rolesToRemove.forEach(user::removeRole);
    User savedUser = userRepository.save(user);
    return userMapper.toResponseDto(savedUser);
  }

  /**
   * Validates a JWT token.
   *
   * @param token the JWT token
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    return jwtService.validateToken(token);
  }

  /**
   * Loads a user by username for Spring Security.
   *
   * @param username the username
   * @return the user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsernameIgnoreCase(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));

    Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
        .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        "", // No password for OAuth users
        authorities
    );
  }

  /**
   * Helper method to find a user by ID.
   *
   * @param id the user ID
   * @return the user
   * @throws ResourceNotFoundException if the user is not found
   */
  private User findUserById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }
}