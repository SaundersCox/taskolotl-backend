package com.saunderscox.taskolotl.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.saunderscox.taskolotl.dto.UserCreateRequestDto;
import com.saunderscox.taskolotl.dto.UserResponseDto;
import com.saunderscox.taskolotl.dto.UserUpdateRequestDto;
import com.saunderscox.taskolotl.entity.Role;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.mapper.UserMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

  private UserMapper userMapper;
  private User user;
  private UUID userId;
  private UUID skillId1;
  private UUID skillId2;
  private UUID roleId1;
  private UUID roleId2;

  @BeforeEach
  void setUp() {
    userMapper = Mappers.getMapper(UserMapper.class);

    // Setup test data
    userId = UUID.randomUUID();
    skillId1 = UUID.randomUUID();
    skillId2 = UUID.randomUUID();
    roleId1 = UUID.randomUUID();
    roleId2 = UUID.randomUUID();

    // Create skills
    Skill skill1 = mock(Skill.class);
    when(skill1.getId()).thenReturn(skillId1);
    Skill skill2 = mock(Skill.class);
    when(skill2.getId()).thenReturn(skillId2);
    Set<Skill> skills = new HashSet<>(Arrays.asList(skill1, skill2));

    // Create roles
    Role role1 = mock(Role.class);
    when(role1.getId()).thenReturn(roleId1);
    Role role2 = mock(Role.class);
    when(role2.getId()).thenReturn(roleId2);
    Set<Role> roles = new HashSet<>(Arrays.asList(role1, role2));

    // Create user
    user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .profileDescription("Test description")
        .profilePictureUrl("https://example.com/pic.jpg")
        .oauthProvider("google")
        .skills(skills)
        .roles(roles)
        .build();

    // Set ID using reflection since it's in the parent class
    try {
      java.lang.reflect.Field idField = user.getClass().getSuperclass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(user, userId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set user ID", e);
    }
  }

  @Test
  void toResponseDto_shouldMapAllFields() {
    // When
    UserResponseDto dto = userMapper.toResponseDto(user);

    // Then
    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(userId);
    assertThat(dto.getUsername()).isEqualTo("testuser");
    assertThat(dto.getEmail()).isEqualTo("test@example.com");
    assertThat(dto.getProfileDescription()).isEqualTo("Test description");
    assertThat(dto.getProfilePictureUrl()).isEqualTo("https://example.com/pic.jpg");
    assertThat(dto.getOauthProvider()).isEqualTo("google");

    // Check skill IDs
    assertThat(dto.getSkillIds()).hasSize(2);
    assertThat(dto.getSkillIds()).contains(skillId1, skillId2);

    // Check role IDs
    assertThat(dto.getRoleIds()).hasSize(2);
    assertThat(dto.getRoleIds()).contains(roleId1, roleId2);
  }

  @Test
  void toResponseDtoList_shouldMapAllUsers() {
    // Given
    User user2 = User.builder()
        .username("testuser2")
        .email("test2@example.com")
        .build();
    List<User> users = Arrays.asList(user, user2);

    // When
    List<UserResponseDto> dtos = userMapper.toResponseDtoList(users);

    // Then
    assertThat(dtos).hasSize(2);
    assertThat(dtos.get(0).getUsername()).isEqualTo("testuser");
    assertThat(dtos.get(1).getUsername()).isEqualTo("testuser2");
  }

  @Test
  void toEntity_shouldMapCreateDtoToEntity() {
    // Given
    UserCreateRequestDto createDto = UserCreateRequestDto.builder()
        .username("newuser")
        .email("new@example.com")
        .oauthId("oauth123")
        .build();

    // When
    User result = userMapper.toEntity(createDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo("newuser");
    assertThat(result.getEmail()).isEqualTo("new@example.com");
    assertThat(result.getOauthId()).isEqualTo("oauth123");
  }

  @Test
  void updateEntityFromDto_shouldUpdateOnlyProvidedFields() {
    // Given
    UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
        .username("updateduser")
        .profileDescription("Updated description")
        .build();

    // When
    userMapper.updateEntityFromDto(updateDto, user);

    // Then
    assertThat(user.getUsername()).isEqualTo("updateduser");
    assertThat(user.getProfileDescription()).isEqualTo("Updated description");
    assertThat(user.getProfilePictureUrl()).isEqualTo("https://example.com/pic.jpg"); // Unchanged
    assertThat(user.getEmail()).isEqualTo("test@example.com"); // Unchanged
  }

  @Test
  void getSkillIds_shouldExtractIdsFromSkills() {
    // When
    Set<UUID> skillIds = userMapper.getSkillIds(user);

    // Then
    assertThat(skillIds).hasSize(2);
    assertThat(skillIds).contains(skillId1, skillId2);
  }

  @Test
  void getRoleIds_shouldExtractIdsFromRoles() {
    // When
    Set<UUID> roleIds = userMapper.getRoleIds(user);

    // Then
    assertThat(roleIds).hasSize(2);
    assertThat(roleIds).contains(roleId1, roleId2);
  }
}