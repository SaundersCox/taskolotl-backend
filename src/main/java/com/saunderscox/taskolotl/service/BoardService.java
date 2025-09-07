package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.dto.BoardCreateRequest;
import com.saunderscox.taskolotl.dto.BoardResponse;
import com.saunderscox.taskolotl.dto.BoardUpdateRequest;
import com.saunderscox.taskolotl.entity.*;
import com.saunderscox.taskolotl.exception.ResourceNotFoundException;
import com.saunderscox.taskolotl.mapper.BoardMapper;
import com.saunderscox.taskolotl.repository.BoardRepository;
import com.saunderscox.taskolotl.repository.RoleRepository;
import com.saunderscox.taskolotl.repository.SkillRepository;
import com.saunderscox.taskolotl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

  public static final String BOARD_NOT_FOUND_WITH_ID = "Board not found with id: ";
  public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
  private final BoardRepository boardRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final SkillRepository skillRepository;
  private final BoardMapper boardMapper;
  private final AuthService authService;

  @Transactional(readOnly = true)
  public Page<BoardResponse> getAllBoards(Pageable pageable) {
    return boardRepository.findAll(pageable)
      .map(boardMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "boardCache", key = "#id")
  public BoardResponse getBoardById(UUID id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id));
    return boardMapper.toResponseDto(board);
  }

  @Transactional
  public BoardResponse createBoard(BoardCreateRequest dto) {
    log.info("Creating board '{}' with {} owners", dto.getTitle(), dto.getOwnerIds().size());

    Board board = boardMapper.toEntity(dto);

    List<User> owners = userRepository.findAllById(dto.getOwnerIds());
    validateAllUsersFound(dto.getOwnerIds(), owners);
    owners.forEach(board::addOwner);

    if (dto.getMemberIds() != null && !dto.getMemberIds().isEmpty()) {
      updateMembers(board, dto.getMemberIds());
    }

    Board savedBoard = boardRepository.save(board);
    return boardMapper.toResponseDto(savedBoard);
  }

  @Transactional
  @CacheEvict(value = "boardCache", key = "#id")
  public BoardResponse updateBoard(UUID id, BoardUpdateRequest dto) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id));

    boardMapper.updateEntityFromDto(dto, board);

    updateOwners(board, dto.getOwnerIds());
    updateMembers(board, dto.getMemberIds());
    updateRoles(board, dto.getRoleIds());
    updateSkills(board, dto.getSkillIds());

    return boardMapper.toResponseDto(boardRepository.save(board));
  }

  private void updateOwners(Board board, Set<UUID> ownerIds) {
    if (ownerIds == null) return;

    List<User> foundUsers = userRepository.findAllById(ownerIds);
    validateAllUsersFound(ownerIds, foundUsers);

    board.getOwners().removeIf(owner -> !ownerIds.contains(owner.getId()));

    foundUsers.forEach(user -> {
      if (board.getOwners().stream().noneMatch(o -> o.getId().equals(user.getId()))) {
        board.addOwner(user);
      }
    });
  }

  private void updateMembers(Board board, Set<UUID> memberIds) {
    if (memberIds == null) return;

    List<User> foundUsers = userRepository.findAllById(memberIds);
    validateAllUsersFound(memberIds, foundUsers);

    board.getMembers().removeIf(member -> !memberIds.contains(member.getId()));

    foundUsers.forEach(user -> {
      if (board.getMembers().stream().noneMatch(m -> m.getId().equals(user.getId()))) {
        board.addMember(user);
      }
    });
  }

  private void updateRoles(Board board, Set<UUID> roleIds) {
    if (roleIds == null) return;

    List<Role> foundRoles = roleRepository.findAllById(roleIds);
    validateAllRolesFound(roleIds, foundRoles);

    board.getRoles().removeIf(role -> !roleIds.contains(role.getId()));

    foundRoles.forEach(role -> {
      if (board.getRoles().stream().noneMatch(r -> r.getId().equals(role.getId()))) {
        board.addRole(role);
      }
    });
  }

  private void updateSkills(Board board, Set<UUID> skillIds) {
    if (skillIds == null) return;

    List<Skill> foundSkills = skillRepository.findAllById(skillIds);
    validateAllSkillsFound(skillIds, foundSkills);

    board.getSkills().removeIf(skill -> !skillIds.contains(skill.getId()));

    foundSkills.forEach(skill -> {
      if (board.getSkills().stream().noneMatch(s -> s.getId().equals(skill.getId()))) {
        board.addSkill(skill);
      }
    });
  }

  private void validateAllUsersFound(Set<UUID> requestedIds, List<User> foundUsers) {
    if (foundUsers.size() != requestedIds.size()) {
      Set<UUID> foundIds = foundUsers.stream().map(User::getId).collect(Collectors.toSet());
      Set<UUID> missingIds = requestedIds.stream()
        .filter(id -> !foundIds.contains(id))
        .collect(Collectors.toSet());
      throw new ResourceNotFoundException("Users not found with ids: " + missingIds);
    }
  }

  private void validateAllRolesFound(Set<UUID> requestedIds, List<Role> foundRoles) {
    if (foundRoles.size() != requestedIds.size()) {
      Set<UUID> foundIds = foundRoles.stream().map(Role::getId).collect(Collectors.toSet());
      Set<UUID> missingIds = requestedIds.stream()
        .filter(id -> !foundIds.contains(id))
        .collect(Collectors.toSet());
      throw new ResourceNotFoundException("Roles not found with ids: " + missingIds);
    }
  }

  private void validateAllSkillsFound(Set<UUID> requestedIds, List<Skill> foundSkills) {
    if (foundSkills.size() != requestedIds.size()) {
      Set<UUID> foundIds = foundSkills.stream().map(Skill::getId).collect(Collectors.toSet());
      Set<UUID> missingIds = requestedIds.stream()
        .filter(id -> !foundIds.contains(id))
        .collect(Collectors.toSet());
      throw new ResourceNotFoundException("Skills not found with ids: " + missingIds);
    }
  }

  @Transactional
  @CacheEvict(value = "boardCache", key = "#id")
  public void deleteBoard(UUID id) {
    log.info("Deleting board {}", id);

    if (!boardRepository.existsById(id)) {
      throw new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id);
    }

    boardRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public Page<BoardResponse> searchBoards(String query, Pageable pageable) {
    log.debug("Searching boards: query='{}', page={}", query, pageable.getPageNumber());
    return boardRepository.findByTitleContainingIgnoreCase(query, pageable)
      .map(boardMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public Page<BoardResponse> getBoardsByOwner(UUID userId, Pageable pageable) {
    return boardRepository.findByOwnersId(userId, pageable)
      .map(boardMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public Page<BoardResponse> getBoardsByMember(UUID userId, Pageable pageable) {
    return boardRepository.findByMembersId(userId, pageable)
      .map(boardMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public Page<BoardResponse> getAccessibleBoards(UUID userId, Pageable pageable) {
    log.debug("Fetching accessible boards for user {}", userId);
    return boardRepository.findByOwnersIdOrMembersId(userId, userId, pageable)
      .map(boardMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public boolean hasAccess(UUID boardId, UUID userId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + boardId));

    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    return board.hasAccess(user);
  }

  @Transactional
  @CacheEvict(value = "boardCache", key = "#boardId")
  public void moveItemToPosition(UUID boardId, UUID boardItemId, int newPosition) {
    log.info("Moving item {} to position {} on board {}", boardItemId, newPosition, boardId);

    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + boardId));

    BoardItem item = board.getBoardItems().stream()
      .filter(i -> i.getId().equals(boardItemId))
      .findFirst()
      .orElseThrow(
        () -> new ResourceNotFoundException("Board item not found with id: " + boardItemId));

    if (newPosition < 0 || newPosition >= board.getBoardItems().size()) {
      throw new IllegalArgumentException(
        "Invalid position: " + newPosition + ". Must be between 0 and " + (board.getBoardItems().size() - 1));
    }

    board.moveItemToPosition(item, newPosition);
    boardRepository.save(board);
  }
}
