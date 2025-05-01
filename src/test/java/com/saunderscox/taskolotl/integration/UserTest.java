package com.saunderscox.taskolotl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.saunderscox.taskolotl.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private SkillRepository skillRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private UserService userService;

  private User testUser;
  private Skill testSkill;
  private Role testRole;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(User.builder()
        .username("testuser")
        .email("test@example.com")
        .build());

    testSkill = skillRepository.save(Skill.builder()
        .name("TestSkill")
        .description("Test skill description")
        .build());

    testRole = roleRepository.save(Role.builder()
        .name("TestRole")
        .description("Test role description")
        .build());
  }

  @Test
  void userCollectionsTest() {
    // Create a user with skills and roles
    User user = userRepository.save(User.builder()
        .username("collectionuser")
        .email("collections@example.com")
        .build());

    // Create skills and roles
    Set<Skill> skills = Set.of(
        skillRepository.save(Skill.builder().name("Skill1").build()),
        skillRepository.save(Skill.builder().name("Skill2").build()),
        skillRepository.save(Skill.builder().name("Skill3").build())
    );

    Set<Role> roles = Set.of(
        roleRepository.save(Role.builder().name("Role1").build()),
        roleRepository.save(Role.builder().name("Role2").build())
    );

    // Add collections
    skills.forEach(user::addSkill);
    roles.forEach(user::addRole);
    user = userRepository.save(user);

    // Verify collections
    assertEquals(3, user.getSkills().size());
    assertEquals(2, user.getRoles().size());

    // Test mapping to DTO
    UserResponseDto dto = userMapper.toResponseDto(user);
    assertEquals(3, dto.getSkillIds().size());
    assertEquals(2, dto.getRoleIds().size());

    // Test bidirectional relationships
    for (Skill skill : skills) {
      assertTrue(skill.getUsers().contains(user));
    }

    for (Role role : roles) {
      assertTrue(role.getUsers().contains(user));
    }
  }

  @Test
  void userServiceCrudTest() {
    // Create user
    UserCreateRequestDto createDto = UserCreateRequestDto.builder()
        .username("newuser")
        .email("new@example.com")
        .build();
    UserResponseDto createdUser = userService.createUser(createDto);
    assertNotNull(createdUser.getId());
    assertEquals("newuser", createdUser.getUsername());

    // Duplicate username should fail
    UserCreateRequestDto duplicateDto = UserCreateRequestDto.builder()
        .username("newuser")
        .email("different@example.com")
        .build();
    assertThrows(DuplicateResourceException.class, () -> userService.createUser(duplicateDto));

    // Get user by ID
    UserResponseDto retrievedUser = userService.getUserById(createdUser.getId());
    assertEquals(createdUser.getId(), retrievedUser.getId());

    // Update user
    UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
        .username("updateduser")
        .profileDescription("Updated description")
        .build();
    UserResponseDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);
    assertEquals("updateduser", updatedUser.getUsername());
    assertEquals("Updated description", updatedUser.getProfileDescription());

    // Delete user
    userService.deleteUser(createdUser.getId());
    assertThrows(ResourceNotFoundException.class,
        () -> userService.getUserById(createdUser.getId()));
  }

  @Test
  void userSkillsAndRolesTest() {
    // Add skills to user
    userService.addSkillsToUser(testUser.getId(), Set.of(testSkill.getId()));
    User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals(1, updatedUser.getSkills().size());
    assertTrue(updatedUser.getSkills().contains(testSkill));
    assertTrue(testSkill.getUsers().contains(updatedUser));

    // Add roles to user
    userService.addRolesToUser(testUser.getId(), Set.of(testRole.getId()));
    updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals(1, updatedUser.getRoles().size());
    assertTrue(updatedUser.getRoles().contains(testRole));
    assertTrue(testRole.getUsers().contains(updatedUser));

    // Remove skill from user
    userService.removeSkillsFromUser(testUser.getId(), Set.of(testSkill.getId()));
    updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals(0, updatedUser.getSkills().size());
    assertFalse(testSkill.getUsers().contains(updatedUser));

    // Remove role from user
    userService.removeRolesFromUser(testUser.getId(), Set.of(testRole.getId()));
    updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
    assertEquals(0, updatedUser.getRoles().size());
    assertFalse(testRole.getUsers().contains(updatedUser));
  }

  @Test
  void userRepositoryTest() {
    // Test case-insensitive queries
    assertTrue(userRepository.existsByUsernameIgnoreCase(testUser.getUsername().toUpperCase()));
    assertTrue(userRepository.existsByEmailIgnoreCase(testUser.getEmail().toUpperCase()));

    // Test pagination
    userRepository.save(User.builder().username("auser").email("a@example.com").build());
    userRepository.save(User.builder().username("buser").email("b@example.com").build());

    var result = userService.getAllUsers(PageRequest.of(0, 2, Sort.by("username")));
    assertEquals(2, result.getContent().size());
    assertTrue(result.getTotalElements() >= 3); // testUser + 2 new users
  }
}