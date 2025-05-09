package com.saunderscox.taskolotl.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.saunderscox.taskolotl.dto.BoardCreateRequestDto;
import com.saunderscox.taskolotl.dto.BoardResponseDto;
import com.saunderscox.taskolotl.dto.BoardUpdateRequestDto;
import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardItem;
import com.saunderscox.taskolotl.entity.BoardType;
import com.saunderscox.taskolotl.entity.Role;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.mapper.BoardMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BoardMapperTest {

  @Autowired
  private BoardMapper boardMapper;

  private Board testBoard;
  private UUID boardId;
  private User owner;
  private User member;
  private BoardItem boardItem;
  private Role role;
  private Skill skill;

  @BeforeEach
  void setUp() {

    // Create test data
    boardId = UUID.randomUUID();

    owner = mock(User.class);
    UUID ownerId = UUID.randomUUID();
    when(owner.getId()).thenReturn(ownerId);

    member = mock(User.class);
    UUID memberId = UUID.randomUUID();
    when(member.getId()).thenReturn(memberId);

    boardItem = mock(BoardItem.class);
    UUID boardItemId = UUID.randomUUID();
    when(boardItem.getId()).thenReturn(boardItemId);

    role = mock(Role.class);
    UUID roleId = UUID.randomUUID();
    when(role.getId()).thenReturn(roleId);

    skill = mock(Skill.class);
    UUID skillId = UUID.randomUUID();
    when(skill.getId()).thenReturn(skillId);

    // Create test board
    testBoard = mock(Board.class);
    when(testBoard.getId()).thenReturn(boardId);
    when(testBoard.getTitle()).thenReturn("Test Board");
    when(testBoard.getBoardType()).thenReturn(BoardType.TASK);
    when(testBoard.getDescription()).thenReturn("Test Description");
    when(testBoard.isVisible()).thenReturn(true);

    Set<User> owners = new HashSet<>();
    owners.add(owner);
    when(testBoard.getOwners()).thenReturn(owners);

    Set<User> members = new HashSet<>();
    members.add(member);
    when(testBoard.getMembers()).thenReturn(members);

    List<BoardItem> boardItems = new ArrayList<>();
    boardItems.add(boardItem);
    when(testBoard.getBoardItems()).thenReturn(boardItems);

    Set<Role> roles = new HashSet<>();
    roles.add(role);
    when(testBoard.getRoles()).thenReturn(roles);

    Set<Skill> skills = new HashSet<>();
    skills.add(skill);
    when(testBoard.getSkills()).thenReturn(skills);
  }

  @Test
  void toResponseDto_shouldMapAllFields() {
    // When
    BoardResponseDto dto = boardMapper.toResponseDto(testBoard);

    // Then
    assertThat(dto)
        .isNotNull()
        .extracting(
            BoardResponseDto::getId,
            BoardResponseDto::getTitle,
            BoardResponseDto::getBoardType,
            BoardResponseDto::getDescription,
            BoardResponseDto::isVisible)
        .containsExactly(
            boardId,
            "Test Board",
            BoardType.TASK,
            "Test Description",
            true);

    assertThat(dto.getOwnerIds())
        .hasSize(1)
        .contains(owner.getId());

    assertThat(dto.getMemberIds())
        .hasSize(1)
        .contains(member.getId());

    assertThat(dto.getBoardItemIds())
        .hasSize(1)
        .contains(boardItem.getId());

    assertThat(dto.getRoleIds())
        .hasSize(1)
        .contains(role.getId());

    assertThat(dto.getSkillIds())
        .hasSize(1)
        .contains(skill.getId());
  }

  @Test
  void toResponseDtoList_shouldMapAllBoards() {
    // Given
    List<Board> boards = new ArrayList<>();
    boards.add(testBoard);

    // When
    List<BoardResponseDto> dtos = boardMapper.toResponseDtoList(boards);

    // Then
    assertThat(dtos)
        .hasSize(1)
        .first()
        .extracting(BoardResponseDto::getId)
        .isEqualTo(boardId);
  }

  @Test
  void toEntity_shouldMapBasicFields() {
    // Given
    BoardCreateRequestDto createDto = new BoardCreateRequestDto();
    createDto.setTitle("New Board");
    createDto.setBoardType(BoardType.STUDY);
    createDto.setDescription("New Description");
    createDto.setVisible(false);

    Set<UUID> ownerIds = new HashSet<>();
    ownerIds.add(UUID.randomUUID());
    createDto.setOwnerIds(ownerIds);

    Set<UUID> memberIds = new HashSet<>();
    memberIds.add(UUID.randomUUID());
    createDto.setMemberIds(memberIds);

    // When
    Board entity = boardMapper.toEntity(createDto);

    // Then
    assertThat(entity)
        .isNotNull()
        .extracting(
            Board::getTitle,
            Board::getBoardType,
            Board::getDescription,
            Board::isVisible)
        .containsExactly(
            "New Board",
            BoardType.STUDY,
            "New Description",
            false);

    // Collections should be empty as they're ignored in the mapping
    assertThat(entity.getOwners()).isEmpty();
    assertThat(entity.getMembers()).isEmpty();
    assertThat(entity.getBoardItems()).isEmpty();
    assertThat(entity.getRoles()).isEmpty();
    assertThat(entity.getSkills()).isEmpty();
  }

  @Test
  void updateEntityFromDto_shouldUpdateOnlyProvidedFields() {
    // Given
    Board board = mock(Board.class);
    when(board.getTitle()).thenReturn("Original Title");
    when(board.getDescription()).thenReturn("Original Description");
    when(board.isVisible()).thenReturn(true);

    BoardUpdateRequestDto updateDto = new BoardUpdateRequestDto();
    updateDto.setTitle("Updated Title");
    // Description is not set, should not be updated
    updateDto.setVisible(false);

    // When
    boardMapper.updateEntityFromDto(updateDto, board);

    // Then - verify the setters were called with correct values
    verify(board).setTitle("Updated Title");
    verify(board).setVisible(false);
    // Verify that setDescription was never called since it wasn't in the DTO
    verify(board, never()).setDescription(any());
  }

  @Test
  void getOwnerIds_shouldReturnCorrectIds() {
    // When
    Set<UUID> ownerIds = boardMapper.getOwnerIds(testBoard);

    // Then
    assertThat(ownerIds)
        .hasSize(1)
        .contains(owner.getId());
  }

  @Test
  void getMemberIds_shouldReturnCorrectIds() {
    // When
    Set<UUID> memberIds = boardMapper.getMemberIds(testBoard);

    // Then
    assertThat(memberIds)
        .hasSize(1)
        .contains(member.getId());
  }

  @Test
  void getBoardItemIds_shouldReturnCorrectIds() {
    // When
    Set<UUID> boardItemIds = boardMapper.getBoardItemIds(testBoard);

    // Then
    assertThat(boardItemIds)
        .hasSize(1)
        .contains(boardItem.getId());
  }

  @Test
  void getRoleIds_shouldReturnCorrectIds() {
    // When
    Set<UUID> roleIds = boardMapper.getRoleIds(testBoard);

    // Then
    assertThat(roleIds)
        .hasSize(1)
        .contains(role.getId());
  }

  @Test
  void getSkillIds_shouldReturnCorrectIds() {
    // When
    Set<UUID> skillIds = boardMapper.getSkillIds(testBoard);

    // Then
    assertThat(skillIds)
        .hasSize(1)
        .contains(skill.getId());
  }
}