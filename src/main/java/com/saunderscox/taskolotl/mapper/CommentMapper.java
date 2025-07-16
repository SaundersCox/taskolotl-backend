//package com.saunderscox.taskolotl.mapper;
//
//import com.saunderscox.taskolotl.dto.CommentCreateRequest;
//import com.saunderscox.taskolotl.dto.CommentResponse;
//import com.saunderscox.taskolotl.dto.CommentUpdateRequest;
//import com.saunderscox.taskolotl.entity.BoardItem;
//import com.saunderscox.taskolotl.entity.Comment;
//import com.saunderscox.taskolotl.service.BoardItemService;
//import com.saunderscox.taskolotl.service.UserService;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//import org.mapstruct.NullValuePropertyMappingStrategy;
//import org.mapstruct.ReportingPolicy;
//
//@Mapper(
//    componentModel = "spring",
//    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
//    unmappedTargetPolicy = ReportingPolicy.IGNORE
//)
//@RequiredArgsConstructor
//public abstract class CommentMapper {
//
//  private final UserService userService;
//  private final BoardItemService boardItemService;
//
//  @Mapping(target = "authorId", source = "author.id")
//  @Mapping(target = "boardItemId", source = "boardItem.id")
//  public abstract CommentResponse toResponseDto(Comment comment);
//
//  public abstract List<CommentResponse> toResponseDtoList(List<Comment> comments);
//
//  @Mapping(target = "author", source = "authorId")
//  @Mapping(target = "boardItem", source = "boardItemId")
//  public abstract Comment toEntity(CommentCreateRequest createDto);
//
//  public abstract void updateEntityFromDto(CommentUpdateRequest updateDto,
//      @MappingTarget Comment comment);
//
//  // Entity resolution methods using services
//  protected User mapToUser(UUID id) {
//    return userService.getUserById(id);
//  }
//
//  protected BoardItem mapToBoardItem(UUID id) {
//    return boardItemService.getBoardItemById(id);
//  }
//}
