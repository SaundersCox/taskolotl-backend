package com.saunderscox.taskolotl.mapper;

import com.saunderscox.taskolotl.dto.BoardCreateRequest;
import com.saunderscox.taskolotl.dto.BoardResponse;
import com.saunderscox.taskolotl.dto.BoardUpdateRequest;
import com.saunderscox.taskolotl.entity.BaseEntity;
import com.saunderscox.taskolotl.entity.Board;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BoardMapper {

  @Mapping(target = "ownerIds", expression = "java(getOwnerIds(board))")
  @Mapping(target = "memberIds", expression = "java(getMemberIds(board))")
  @Mapping(target = "boardItemIds", expression = "java(getBoardItemIds(board))")
  @Mapping(target = "roleIds", expression = "java(getRoleIds(board))")
  @Mapping(target = "skillIds", expression = "java(getSkillIds(board))")
  BoardResponse toResponseDto(Board board);

  List<BoardResponse> toResponseDtoList(List<Board> boards);

  @Mapping(target = "owners", ignore = true)
  @Mapping(target = "members", ignore = true)
  @Mapping(target = "boardItems", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "skills", ignore = true)
  Board toEntity(BoardCreateRequest createDto);

  @Mapping(target = "owners", ignore = true)
  @Mapping(target = "members", ignore = true)
  @Mapping(target = "boardItems", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "skills", ignore = true)
  void updateEntityFromDto(BoardUpdateRequest updateDto, @MappingTarget Board board);

  default Set<UUID> getOwnerIds(Board board) {
    return board.getOwners().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }

  default Set<UUID> getMemberIds(Board board) {
    return board.getMembers().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }

  default Set<UUID> getBoardItemIds(Board board) {
    return board.getBoardItems().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }

  default Set<UUID> getRoleIds(Board board) {
    return board.getRoles().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }

  default Set<UUID> getSkillIds(Board board) {
    return board.getSkills().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }
}
