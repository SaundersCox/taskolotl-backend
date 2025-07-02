package com.saunderscox.taskolotl.service;

import com.saunderscox.taskolotl.dto.BoardCreateRequestDto;
import com.saunderscox.taskolotl.dto.BoardResponseDto;
import com.saunderscox.taskolotl.dto.BoardUpdateRequestDto;
import com.saunderscox.taskolotl.entity.BaseEntity;
import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardItem;
import com.saunderscox.taskolotl.entity.User;
import com.saunderscox.taskolotl.exception.ResourceNotFoundException;
import com.saunderscox.taskolotl.mapper.BoardMapper;
import com.saunderscox.taskolotl.repository.BoardRepository;
import com.saunderscox.taskolotl.repository.RoleRepository;
import com.saunderscox.taskolotl.repository.SkillRepository;
import com.saunderscox.taskolotl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

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

    // Add owners (required)
    for (UUID ownerId : dto.getOwnerIds()) {
      User owner = userRepository.findById(ownerId)
          .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + ownerId));
      board.addOwner(owner);
    }

    // Add members if provided
    if (dto.getMemberIds() != null && !dto.getMemberIds().isEmpty()) {
      updateCollection(board.getMembers(), dto.getMemberIds(), userRepository, board::addMember);
    }

    Board savedBoard = boardRepository.save(board);
    return boardMapper.toResponseDto(savedBoard);
  }

  public BoardResponseDto updateBoard(UUID id, BoardUpdateRequestDto dto) {
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(BOARD_NOT_FOUND_WITH_ID + id));

    // Update basic properties
    if (dto.getTitle() != null) {
      board.setTitle(dto.getTitle());
    }
    if (dto.getDescription() != null) {
      board.setDescription(dto.getDescription());
    }
    if (dto.getVisible() != null) {
      board.setVisible(dto.getVisible());
    }

    // Update collections with helper methods
    if (dto.getOwnerIds() != null) {
      updateCollection(board.getOwners(), dto.getOwnerIds(), userRepository, board::addOwner);
    }
    if (dto.getMemberIds() != null) {
      updateCollection(board.getMembers(), dto.getMemberIds(), userRepository, board::addMember);
    }
    if (dto.getRoleIds() != null) {
      updateCollection(board.getRoles(), dto.getRoleIds(), roleRepository, board::addRole);
    }
    if (dto.getSkillIds() != null) {
      updateCollection(board.getSkills(), dto.getSkillIds(), skillRepository, board::addSkill);
    }

    return boardMapper.toResponseDto(boardRepository.save(board));
  }

  // Cleaner generic helper with type bound
  private <T extends BaseEntity> void updateCollection(Set<T> currentItems, Set<UUID> newItemIds,
                                                       JpaRepository<T, UUID> repository, Consumer<T> adder) {
    // Remove items not in new list
    currentItems.removeIf(item -> !newItemIds.contains(item.getId()));

    // Add new items
    for (UUID id : newItemIds) {
      boolean exists = currentItems.stream().anyMatch(item -> item.getId().equals(id));
      if (!exists) {
        repository.findById(id).ifPresent(adder);
      }
    }
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
