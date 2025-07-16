//package com.saunderscox.taskolotl.integration;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import com.saunderscox.taskolotl.dto.CommentCreateRequest;
//import com.saunderscox.taskolotl.dto.CommentResponse;
//import com.saunderscox.taskolotl.dto.CommentUpdateRequest;
//import com.saunderscox.taskolotl.entity.BoardItem;
//import com.saunderscox.taskolotl.entity.Comment;
//import com.saunderscox.taskolotl.entity.User;
//import com.saunderscox.taskolotl.mapper.CommentMapper;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class CommentMapperTest {
//
//  @Autowired
//  private CommentMapper commentMapper;
//
//  private Comment comment;
//  private UUID commentId;
//  private UUID authorId;
//  private UUID boardItemId;
//  private Set<String> tags;
//
//  @BeforeEach
//  void setUp() {
//    // Setup test data
//    commentId = UUID.randomUUID();
//    authorId = UUID.randomUUID();
//    boardItemId = UUID.randomUUID();
//    tags = new HashSet<>(Arrays.asList("important", "bug", "frontend"));
//
//    // Create author
//    User author = mock(User.class);
//    when(author.getId()).thenReturn(authorId);
//
//    // Create board item
//    BoardItem boardItem = mock(BoardItem.class);
//    when(boardItem.getId()).thenReturn(boardItemId);
//
//    // Create comment
//    comment = Comment.builder()
//        .id(commentId)
//        .author(author)
//        .boardItem(boardItem)
//        .description("This is a test comment")
//        .tags(tags)
//        .build();
//  }
//
//  @Test
//  void toResponseDto_shouldMapAllFields() {
//    // When
//    CommentResponse dto = commentMapper.toResponseDto(comment);
//
//    // Then
//    assertThat(dto)
//        .isNotNull()
//        .satisfies(d -> {
//          assertThat(d.getId()).isEqualTo(commentId);
//          assertThat(d.getAuthorId()).isEqualTo(authorId);
//          assertThat(d.getBoardItemId()).isEqualTo(boardItemId);
//          assertThat(d.getDescription()).isEqualTo("This is a test comment");
//          assertThat(d.getTags())
//              .hasSize(3)
//              .contains("important", "bug", "frontend");
//        });
//  }
//
//  @Test
//  void toResponseDtoList_shouldMapAllComments() {
//    // Given
//    User author2 = mock(User.class);
//    when(author2.getId()).thenReturn(UUID.randomUUID());
//
//    BoardItem boardItem2 = mock(BoardItem.class);
//    when(boardItem2.getId()).thenReturn(UUID.randomUUID());
//
//    Comment comment2 = Comment.builder()
//        .author(author2)
//        .boardItem(boardItem2)
//        .description("Second test comment")
//        .build();
//
//    List<Comment> comments = Arrays.asList(comment, comment2);
//
//    // When
//    List<CommentResponse> dtos = commentMapper.toResponseDtoList(comments);
//
//    // Then
//    assertThat(dtos)
//        .hasSize(2)
//        .satisfies(list -> {
//          assertThat(list.get(0).getDescription()).isEqualTo("This is a test comment");
//          assertThat(list.get(1).getDescription()).isEqualTo("Second test comment");
//        });
//  }
//
//  @Test
//  void toEntity_shouldMapCreateDtoToEntity() {
//    // Given
//    Set<String> newTags = new HashSet<>(Arrays.asList("priority", "backend"));
//    CommentCreateRequest createDto = CommentCreateRequest.builder()
//        .authorId(UUID.randomUUID())
//        .boardItemId(UUID.randomUUID())
//        .description("New comment from DTO")
//        .tags(newTags)
//        .build();
//
//    // When
//    Comment result = commentMapper.toEntity(createDto);
//
//    // Then
//    assertThat(result)
//        .isNotNull()
//        .satisfies(c -> {
//          assertThat(c.getDescription()).isEqualTo("New comment from DTO");
//          assertThat(c.getTags())
//              .hasSize(2)
//              .contains("priority", "backend");
//          // Author and BoardItem should be null as they're ignored in the mapping
//          assertThat(c.getAuthor()).isNull();
//          assertThat(c.getBoardItem()).isNull();
//        });
//  }
//
//  @ParameterizedTest
//  @CsvSource({
//      "Updated comment, true",
//      "Updated comment, false",
//      ", true"
//  })
//  void updateEntityFromDto_shouldUpdateOnlyProvidedFields(
//      String description, boolean includeTags) {
//    // Given
//    String originalDescription = comment.getDescription();
//    Set<String> originalTags = new HashSet<>(comment.getTags());
//
//    Set<String> newTags = includeTags ?
//        new HashSet<>(Arrays.asList("updated", "review")) : null;
//
//    CommentUpdateRequest updateDto = CommentUpdateRequest.builder()
//        .description(description)
//        .tags(newTags)
//        .build();
//
//    // When
//    commentMapper.updateEntityFromDto(updateDto, comment);
//
//    // Then
//    assertThat(comment)
//        .satisfies(c -> {
//          assertThat(c.getDescription()).isEqualTo(
//              description != null ? description : originalDescription);
//
//          if (includeTags) {
//            assertThat(c.getTags())
//                .hasSize(2)
//                .contains("updated", "review");
//          } else {
//            assertThat(c.getTags()).isEqualTo(originalTags);
//          }
//
//          // Verify relationships weren't changed
//          assertThat(c.getAuthor().getId()).isEqualTo(authorId);
//          assertThat(c.getBoardItem().getId()).isEqualTo(boardItemId);
//        });
//  }
//}
