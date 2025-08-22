package com.saunderscox.taskolotl.controller;

import com.saunderscox.taskolotl.dto.BoardCreateRequest;
import com.saunderscox.taskolotl.dto.BoardResponse;
import com.saunderscox.taskolotl.dto.BoardUpdateRequest;
import com.saunderscox.taskolotl.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Board", description = "Board management API")
@Validated
public class BoardController {

  private final BoardService boardService;

  // Basic CRUD Operations
  @GetMapping
  @Operation(summary = "Get all boards")
  @Tag(name = "Board - CRUD")
  @PageableAsQueryParam
  public ResponseEntity<Page<BoardResponse>> getAllBoards(
    Pageable pageable) {
    return ResponseEntity.ok(boardService.getAllBoards(pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a board by ID")
  @Tag(name = "Board - CRUD")
  public ResponseEntity<BoardResponse> getBoardById(
    @PathVariable UUID id) {
    return ResponseEntity.ok(boardService.getBoardById(id));
  }

  @PostMapping
  @Operation(summary = "Create a new board")
  @Tag(name = "Board - CRUD")
  public ResponseEntity<BoardResponse> createBoard(
    @Valid @RequestBody BoardCreateRequest dto) {
    BoardResponse result = boardService.createBoard(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @PutMapping("/{id}")
  @PreAuthorize("@boardService.currentUserHasAccess(#id)")
  @Operation(summary = "Update a board")
  @Tag(name = "Board - CRUD")
  public ResponseEntity<BoardResponse> updateBoard(
    @PathVariable UUID id,
    @Valid @RequestBody BoardUpdateRequest dto) {
    return ResponseEntity.ok(boardService.updateBoard(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@boardService.currentUserHasAccess(#id)")
  @Operation(summary = "Delete a board")
  @Tag(name = "Board - CRUD")
  public ResponseEntity<Void> deleteBoard(
    @PathVariable UUID id) {
    boardService.deleteBoard(id);
    return ResponseEntity.noContent().build();
  }

  // Search Operations
  @GetMapping("/search")
  @Operation(summary = "Search boards")
  @Tag(name = "Board - Search")
  @PageableAsQueryParam
  public ResponseEntity<Page<BoardResponse>> searchBoards(
    @RequestParam @NotBlank(message = "Search query cannot be empty")
    @Size(min = 3, max = 100, message = "Search query must be between 3 and 100 characters")
    String query,
    Pageable pageable) {
    return ResponseEntity.ok(boardService.searchBoards(query, pageable));
  }

  // User-Related Queries
  @GetMapping("/owner/{userId}")
  @Operation(summary = "Get boards by owner")
  @Tag(name = "Board - User Queries")
  @PageableAsQueryParam
  public ResponseEntity<Page<BoardResponse>> getBoardsByOwner(
    @PathVariable UUID userId,
    Pageable pageable) {
    return ResponseEntity.ok(boardService.getBoardsByOwner(userId, pageable));
  }

  @GetMapping("/member/{userId}")
  @Operation(summary = "Get boards by member")
  @Tag(name = "Board - User Queries")
  @PageableAsQueryParam
  public ResponseEntity<Page<BoardResponse>> getBoardsByMember(
    @PathVariable UUID userId,
    Pageable pageable) {
    return ResponseEntity.ok(boardService.getBoardsByMember(userId, pageable));
  }

  @GetMapping("/accessible/{userId}")
  @Operation(summary = "Get accessible boards")
  @Tag(name = "Board - User Queries")
  @PageableAsQueryParam
  public ResponseEntity<Page<BoardResponse>> getAccessibleBoards(
    @PathVariable UUID userId,
    Pageable pageable) {
    return ResponseEntity.ok(boardService.getAccessibleBoards(userId, pageable));
  }

  // Specialized Operations
  @PostMapping("/{boardId}/items/{boardItemId}/move")
  @PreAuthorize("@boardService.currentUserHasAccess(#boardId)")
  @Operation(summary = "Move a board item")
  @Tag(name = "Board - Operations")
  public ResponseEntity<Void> moveItemToPosition(
    @PathVariable UUID boardId,
    @PathVariable UUID boardItemId,
    @RequestParam @Min(value = 0, message = "Position must be a non-negative number") int position) {
    boardService.moveItemToPosition(boardId, boardItemId, position);
    return ResponseEntity.ok().build();
  }
}
