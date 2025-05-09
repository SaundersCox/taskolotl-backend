package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.BoardCreateRequestDto;
import com.saunderscox.taskolotl.dto.BoardResponseDto;
import com.saunderscox.taskolotl.dto.BoardUpdateRequestDto;
import com.saunderscox.taskolotl.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing board operations.
 */
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Board", description = "Board management API")
public class BoardController {

  private final BoardService boardService;

  /**
   * GET /api/boards : Get all boards with pagination
   *
   * @param pageable pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of boards
   */
  @GetMapping
  @Operation(summary = "Get all boards", description = "Returns a paginated list of all boards")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved boards",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Page.class)))
  public ResponseEntity<Page<BoardResponseDto>> getAllBoards(
      @Parameter(description = "Pagination information") Pageable pageable) {
    Page<BoardResponseDto> page = boardService.getAllBoards(pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * GET /api/boards/:id : Get a board by id
   *
   * @param id the id of the board to retrieve
   * @return the ResponseEntity with status 200 (OK) and the board, or with status 404 (Not Found)
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get a board by ID", description = "Returns a board based on its ID")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved board",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = BoardResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Board not found")
  public ResponseEntity<BoardResponseDto> getBoardById(
      @Parameter(description = "ID of the board to retrieve") @PathVariable UUID id) {
    BoardResponseDto board = boardService.getBoardById(id);
    return ResponseEntity.ok(board);
  }

  /**
   * POST /api/boards : Create a new board
   *
   * @param dto the board to create
   * @return the ResponseEntity with status 201 (Created) and the new board
   */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Create a new board", description = "Creates a new board and returns the created board")
  @ApiResponse(responseCode = "201", description = "Board successfully created",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = BoardResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid input")
  @ApiResponse(responseCode = "401", description = "Unauthorized")
  public ResponseEntity<BoardResponseDto> createBoard(
      @Parameter(description = "Board to create") @Valid @RequestBody BoardCreateRequestDto dto) {
    BoardResponseDto result = boardService.createBoard(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  /**
   * PUT /api/boards/:id : Update an existing board
   *
   * @param id  the id of the board to update
   * @param dto the board to update
   * @return the ResponseEntity with status 200 (OK) and the updated board
   */
  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated() && @boardService.currentUserHasAccess(#id)")
  @Operation(summary = "Update a board", description = "Updates an existing board and returns the updated board")
  @ApiResponse(responseCode = "200", description = "Board successfully updated",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = BoardResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Invalid input")
  @ApiResponse(responseCode = "401", description = "Unauthorized")
  @ApiResponse(responseCode = "403", description = "Forbidden")
  @ApiResponse(responseCode = "404", description = "Board not found")
  public ResponseEntity<BoardResponseDto> updateBoard(
      @Parameter(description = "ID of the board to update") @PathVariable UUID id,
      @Parameter(description = "Updated board information") @Valid @RequestBody BoardUpdateRequestDto dto) {
    BoardResponseDto result = boardService.updateBoard(id, dto);
    return ResponseEntity.ok(result);
  }

  /**
   * DELETE /api/boards/:id : Delete a board
   *
   * @param id the id of the board to delete
   * @return the ResponseEntity with status 204 (NO_CONTENT)
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated() && @boardService.currentUserHasAccess(#id)")
  @Operation(summary = "Delete a board", description = "Deletes a board by its ID")
  @ApiResponse(responseCode = "204", description = "Board successfully deleted")
  @ApiResponse(responseCode = "401", description = "Unauthorized")
  @ApiResponse(responseCode = "403", description = "Forbidden")
  @ApiResponse(responseCode = "404", description = "Board not found")
  public ResponseEntity<Void> deleteBoard(
      @Parameter(description = "ID of the board to delete") @PathVariable UUID id) {
    boardService.deleteBoard(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * GET /api/boards/search : Search boards by title
   *
   * @param query    the search query
   * @param pageable pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of boards
   */
  @GetMapping("/search")
  @Operation(summary = "Search boards", description = "Searches boards by title and returns a paginated list")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved boards",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Page.class)))
  public ResponseEntity<Page<BoardResponseDto>> searchBoards(
      @Parameter(description = "Search query") @RequestParam String query,
      @Parameter(description = "Pagination information") Pageable pageable) {
    Page<BoardResponseDto> page = boardService.searchBoards(query, pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * GET /api/boards/owner/:userId : Get boards owned by a user
   *
   * @param userId   the id of the user
   * @param pageable pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of boards
   */
  @GetMapping("/owner/{userId}")
  @Operation(summary = "Get boards by owner", description = "Returns a paginated list of boards owned by a specific user")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved boards",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Page.class)))
  public ResponseEntity<Page<BoardResponseDto>> getBoardsByOwner(
      @Parameter(description = "ID of the owner") @PathVariable UUID userId,
      @Parameter(description = "Pagination information") Pageable pageable) {
    Page<BoardResponseDto> page = boardService.getBoardsByOwner(userId, pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * GET /api/boards/member/:userId : Get boards where a user is a member
   *
   * @param userId   the id of the user
   * @param pageable pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of boards
   */
  @GetMapping("/member/{userId}")
  @Operation(summary = "Get boards by member", description = "Returns a paginated list of boards where a specific user is a member")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved boards",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Page.class)))
  public ResponseEntity<Page<BoardResponseDto>> getBoardsByMember(
      @Parameter(description = "ID of the member") @PathVariable UUID userId,
      @Parameter(description = "Pagination information") Pageable pageable) {
    Page<BoardResponseDto> page = boardService.getBoardsByMember(userId, pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * GET /api/boards/accessible/:userId : Get boards accessible to a user
   *
   * @param userId   the id of the user
   * @param pageable pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of boards
   */
  @GetMapping("/accessible/{userId}")
  @Operation(summary = "Get accessible boards", description = "Returns a paginated list of boards accessible to a specific user (as owner or member)")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved boards",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Page.class)))
  public ResponseEntity<Page<BoardResponseDto>> getAccessibleBoards(
      @Parameter(description = "ID of the user") @PathVariable UUID userId,
      @Parameter(description = "Pagination information") Pageable pageable) {
    Page<BoardResponseDto> page = boardService.getAccessibleBoards(userId, pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * POST /api/boards/:boardId/items/:itemId/move : Move a board item to a new position
   *
   * @param boardId     the id of the board
   * @param boardItemId the id of the board item
   * @param position    the new position
   * @return the ResponseEntity with status 200 (OK)
   */
  @PostMapping("/{boardId}/items/{boardItemId}/move")
  @PreAuthorize("isAuthenticated() && @boardService.currentUserHasAccess(#boardId)")
  @Operation(summary = "Move a board item", description = "Moves a board item to a new position within the board")
  @ApiResponse(responseCode = "200", description = "Item successfully moved")
  @ApiResponse(responseCode = "401", description = "Unauthorized")
  @ApiResponse(responseCode = "403", description = "Forbidden")
  @ApiResponse(responseCode = "404", description = "Board or item not found")
  public ResponseEntity<Void> moveItemToPosition(
      @Parameter(description = "ID of the board") @PathVariable UUID boardId,
      @Parameter(description = "ID of the board item") @PathVariable UUID boardItemId,
      @Parameter(description = "New position") @RequestParam int position) {
    boardService.moveItemToPosition(boardId, boardItemId, position);
    return ResponseEntity.ok().build();
  }
}