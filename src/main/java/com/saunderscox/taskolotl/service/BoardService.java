package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.dto.BoardCreateRequestDto;
import com.saunderscox.taskolotl.dto.BoardResponseDto;
import com.saunderscox.taskolotl.dto.BoardUpdateRequestDto;
import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardItem;
import com.saunderscox.taskolotl.entity.Role;
import com.saunderscox.taskolotl.entity.Skill;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.exception.ResourceNotFoundException;
import com.saunderscox.taskolotl.mapper.BoardMapper;
import com.saunderscox.taskolotl.repository.BoardRepository;
import com.saunderscox.taskolotl.repository.RoleRepository;
import com.saunderscox.taskolotl.repository.SkillRepository;
import com.saunderscox.taskolotl.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing board operations including CRUD operations and board-related business
 * logic.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

  public static final String BOARD_NOT_FOUND_WITH_ID = "Board not found with id: ";
  public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
  private final BoardRepository boardRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final SkillRepository skillRepository;
  private final BoardMapper boardMapper;

  /**
   * Retrieves all boards from the database
   *
   * @param pageable Pagination information
   * @return Page of boards as DTOs
   */
  public Page<BoardResponseDto> getAllBoards(Pageable pageable) {
    log.debug("Fetching all boards with pagination: page={}, size={}",
        pageable.getPageNumber(), pageable.getPageSize());
    return boardRepository.findAll(pageable)
        .map(boardMapper::toResponseDto);
  }

  /**
   * Retrieves a board by its ID
   *
   * @param id The board ID
   * @return The board as a DTO
   * @throws ResourceNotFoundException if the board doesn't exist
   */
  public BoardResponseDto getBoardById(UUID id) {
    log.debug("Fetching board with ID: {}", id);
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id));
    return boardMapper.toResponseDto(board);
  }

  /**
   * Creates a new board
   *
   * @param dto The board creation request
   * @return The created board as a DTO
   * @throws ResourceNotFoundException if any referenced entities don't exist
   */
  public BoardResponseDto createBoard(BoardCreateRequestDto dto) {
    log.info("Creating new board with title: {}", dto.getTitle());

    Board board = boardMapper.toEntity(dto);

    // Add owners
    Set<User> owners = new HashSet<>();
    for (UUID ownerId : dto.getOwnerIds()) {
      User owner = userRepository.findById(ownerId)
          .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + ownerId));
      owners.add(owner);
    }

    // Add members if provided
    Set<User> members = new HashSet<>();
    if (dto.getMemberIds() != null) {
      for (UUID memberId : dto.getMemberIds()) {
        User member = userRepository.findById(memberId)
            .orElseThrow(
                () -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + memberId));
        members.add(member);
      }
    }

    // Set collections
    owners.forEach(board::addOwner);
    members.forEach(board::addMember);

    Board savedBoard = boardRepository.save(board);
    log.info("Board created successfully with ID: {}", savedBoard.getId());
    return boardMapper.toResponseDto(savedBoard);
  }

  /**
   * Updates an existing board
   *
   * @param id  The board ID
   * @param dto The update request
   * @return The updated board as a DTO
   * @throws ResourceNotFoundException if the board doesn't exist
   */
  public BoardResponseDto updateBoard(UUID id, BoardUpdateRequestDto dto) {
    log.info("Updating board with ID: {}", id);

    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id));

    boardMapper.updateEntityFromDto(dto, board);

    // Update owners if provided
    if (dto.getOwnerIds() != null) {
      // Remove owners not in the new list
      Set<User> currentOwners = new HashSet<>(board.getOwners());
      for (User owner : currentOwners) {
        if (!dto.getOwnerIds().contains(owner.getId())) {
          board.removeOwner(owner);
        }
      }

      // Add new owners
      for (UUID ownerId : dto.getOwnerIds()) {
        if (board.getOwners().stream().noneMatch(o -> o.getId().equals(ownerId))) {
          userRepository.findById(ownerId).ifPresent(board::addOwner);
        }
      }
    }

    // Update members if provided
    if (dto.getMemberIds() != null) {
      // Remove members not in the new list
      Set<User> currentMembers = new HashSet<>(board.getMembers());
      for (User member : currentMembers) {
        if (!dto.getMemberIds().contains(member.getId())) {
          board.removeMember(member);
        }
      }

      // Add new members
      for (UUID memberId : dto.getMemberIds()) {
        if (board.getMembers().stream().noneMatch(m -> m.getId().equals(memberId))) {
          userRepository.findById(memberId).ifPresent(board::addMember);
        }
      }
    }

    // Update roles if provided
    if (dto.getRoleIds() != null) {
      // Remove roles not in the new list
      Set<Role> currentRoles = new HashSet<>(board.getRoles());
      for (Role role : currentRoles) {
        if (!dto.getRoleIds().contains(role.getId())) {
          board.removeRole(role);
        }
      }

      // Add new roles
      for (UUID roleId : dto.getRoleIds()) {
        if (board.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
          roleRepository.findById(roleId).ifPresent(board::addRole);
        }
      }
    }

    // Update skills if provided
    if (dto.getSkillIds() != null) {
      // Remove skills not in the new list
      Set<Skill> currentSkills = new HashSet<>(board.getSkills());
      for (Skill skill : currentSkills) {
        if (!dto.getSkillIds().contains(skill.getId())) {
          board.removeSkill(skill);
        }
      }

      // Add new skills
      for (UUID skillId : dto.getSkillIds()) {
        if (board.getSkills().stream().noneMatch(s -> s.getId().equals(skillId))) {
          skillRepository.findById(skillId).ifPresent(board::addSkill);
        }
      }
    }

    Board updatedBoard = boardRepository.save(board);
    log.info("Board updated successfully: {}", updatedBoard.getTitle());
    return boardMapper.toResponseDto(updatedBoard);
  }

  /**
   * Deletes a board
   *
   * @param id The board ID
   * @throws ResourceNotFoundException if the board doesn't exist
   */
  public void deleteBoard(UUID id) {
    log.info("Deleting board with ID: {}", id);

    if (!boardRepository.existsById(id)) {
      throw new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id);
    }

    boardRepository.deleteById(id);
    log.info("Board deleted successfully");
  }

  /**
   * Searches for boards by title
   *
   * @param query    The search query
   * @param pageable Pagination information
   * @return Page of matching boards as DTOs
   */
  public Page<BoardResponseDto> searchBoards(String query, Pageable pageable) {
    log.debug("Searching boards with query: {} and pagination: page={}, size={}",
        query, pageable.getPageNumber(), pageable.getPageSize());

    return boardRepository.findByTitleContainingIgnoreCase(query, pageable)
        .map(boardMapper::toResponseDto);
  }

  /**
   * Gets boards owned by a specific user
   *
   * @param userId   The user ID
   * @param pageable Pagination information
   * @return Page of boards owned by the user
   */
  public Page<BoardResponseDto> getBoardsByOwner(UUID userId, Pageable pageable) {
    log.debug("Fetching boards owned by user: {}", userId);
    return boardRepository.findByOwnersId(userId, pageable)
        .map(boardMapper::toResponseDto);
  }

  /**
   * Gets boards where a user is a member
   *
   * @param userId   The user ID
   * @param pageable Pagination information
   * @return Page of boards where the user is a member
   */
  public Page<BoardResponseDto> getBoardsByMember(UUID userId, Pageable pageable) {
    log.debug("Fetching boards where user {} is a member", userId);
    return boardRepository.findByMembersId(userId, pageable)
        .map(boardMapper::toResponseDto);
  }

  /**
   * Gets all boards accessible to a user (either as owner or member)
   *
   * @param userId   The user ID
   * @param pageable Pagination information
   * @return Page of accessible boards
   */
  public Page<BoardResponseDto> getAccessibleBoards(UUID userId, Pageable pageable) {
    log.debug("Fetching boards accessible to user: {}", userId);
    return boardRepository.findByOwnersIdOrMembersId(userId, userId, pageable)
        .map(boardMapper::toResponseDto);
  }

  /**
   * Checks if a user has access to a board (is either an owner or member)
   *
   * @param boardId The board ID
   * @param userId  The user ID
   * @return true if the user has access
   */
  public boolean hasAccess(UUID boardId, UUID userId) {
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + boardId));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    return board.hasAccess(user);
  }

  /**
   * Checks if the current authenticated user has access to a board
   *
   * @param boardId The board ID
   * @return true if the current user has access
   */
  public boolean currentUserHasAccess(UUID boardId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

    return hasAccess(boardId, user.getId());
  }

  /**
   * Moves a board item to a new position
   *
   * @param boardId     The board ID
   * @param boardItemId The board item ID
   * @param newPosition The new position
   * @throws ResourceNotFoundException if the board or item doesn't exist
   */
  public void moveItemToPosition(UUID boardId, UUID boardItemId, int newPosition) {
    log.info("Moving item {} to position {} on board {}", boardItemId, newPosition, boardId);

    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + boardId));

    BoardItem item = board.getBoardItems().stream()
        .filter(i -> i.getId().equals(boardItemId))
        .findFirst()
        .orElseThrow(
            () -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + boardItemId));

    board.moveItemToPosition(item, newPosition);
    boardRepository.save(board);
    log.info("Item moved successfully");
  }
}