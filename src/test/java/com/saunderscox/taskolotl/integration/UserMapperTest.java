package com.saunderscox.taskolotl.integration;

import com.saunderscox.taskolotl.dto.UserCreateRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.dto.UserUpdateRequest;
import com.saunderscox.taskolotl.entity.Role;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserMapperTest {

  @Autowired
  private UserMapper userMapper;

  private User user;
  private UUID userId;
  private UUID skillId1;
  private UUID skillId2;
  private UUID roleId1;
  private UUID roleId2;

  @BeforeEach
  void setUp() {

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
        .id(userId)
        .username("testuser")
        .email("test@example.com")
        .profileDescription("Test description")
        .profilePictureUrl("https://example.com/pic.jpg")
        .oauthProvider("google")
        .skills(skills)
        .roles(roles)
        .build();
  }

  @Test
  void toResponseDto_shouldMapAllFields() {
    // When
    UserResponse dto = userMapper.toResponseDto(user);

    // Then
    assertThat(dto)
        .isNotNull()
        .satisfies(d -> {
          assertThat(d.getId()).isEqualTo(userId);
          assertThat(d.getUsername()).isEqualTo("testuser");
          assertThat(d.getEmail()).isEqualTo("test@example.com");
          assertThat(d.getProfileDescription()).isEqualTo("Test description");
          assertThat(d.getProfilePictureUrl()).isEqualTo("https://example.com/pic.jpg");
          assertThat(d.getOauthProvider()).isEqualTo("google");

          assertThat(d.getSkillIds())
              .hasSize(2)
              .contains(skillId1, skillId2);

          assertThat(d.getRoleIds())
              .hasSize(2)
              .contains(roleId1, roleId2);
        });
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
    List<UserResponse> dtos = userMapper.toResponseDtoList(users);

    // Then
    assertThat(dtos)
        .hasSize(2)
        .satisfies(list -> {
          assertThat(list.get(0).getUsername()).isEqualTo("testuser");
          assertThat(list.get(1).getUsername()).isEqualTo("testuser2");
        });
  }

  @Test
  void toEntity_shouldMapCreateDtoToEntity() {
    // Given
    UserCreateRequest createDto = UserCreateRequest.builder()
        .username("newuser")
        .email("new@example.com")
        .oauthId("oauth123")
        .build();

    // When
    User result = userMapper.toEntity(createDto);

    // Then
    assertThat(result)
        .isNotNull()
        .satisfies(u -> {
          assertThat(u.getUsername()).isEqualTo("newuser");
          assertThat(u.getEmail()).isEqualTo("new@example.com");
          assertThat(u.getOauthId()).isEqualTo("oauth123");
        });
  }

  @ParameterizedTest
  @CsvSource({
      "updateduser, Updated description, https://updated.example.com/pic.jpg",
      "updateduser, , ",
      ", Updated description, ",
      ", , https://updated.example.com/pic.jpg"
  })
  void updateEntityFromDto_shouldUpdateOnlyProvidedFields(
      String username, String description, String pictureUrl) {
    // Given
    String originalUsername = user.getUsername();
    String originalDescription = user.getProfileDescription();
    String originalPictureUrl = user.getProfilePictureUrl();

    UserUpdateRequest updateDto = UserUpdateRequest.builder()
        .username(username)
        .profileDescription(description)
        .profilePictureUrl(pictureUrl)
        .build();

    // When
    userMapper.updateEntityFromDto(updateDto, user);

    // Then
    assertThat(user)
        .satisfies(u -> {
          assertThat(u.getUsername()).isEqualTo(username != null ? username : originalUsername);
          assertThat(u.getProfileDescription()).isEqualTo(
              description != null ? description : originalDescription);
          assertThat(u.getProfilePictureUrl()).isEqualTo(
              pictureUrl != null ? pictureUrl : originalPictureUrl);
          // Verify email wasn't changed
          assertThat(u.getEmail()).isEqualTo("test@example.com");
        });
  }

  @Test
  void getSkillIds_shouldExtractIdsFromSkills() {
    // When
    Set<UUID> skillIds = userMapper.getSkillIds(user);

    // Then
    assertThat(skillIds)
        .hasSize(2)
        .contains(skillId1, skillId2);
  }

  @Test
  void getRoleIds_shouldExtractIdsFromRoles() {
    // When
    Set<UUID> roleIds = userMapper.getRoleIds(user);

    // Then
    assertThat(roleIds)
        .hasSize(2)
        .contains(roleId1, roleId2);
  }
}
