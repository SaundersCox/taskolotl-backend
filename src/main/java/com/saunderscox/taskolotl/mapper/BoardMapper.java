package com.saunderscox.taskolotl.mapper;

import com.saunderscox.taskolotl.dto.BoardCreateRequestDto;
import com.saunderscox.taskolotl.dto.BoardResponseDto;
import com.saunderscox.taskolotl.dto.BoardUpdateRequestDto;
import com.saunderscox.taskolotl.entity.BaseEntity;
import com.saunderscox.taskolotl.entity.Board;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between Board entities and DTOs.
 */
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
  BoardResponseDto toResponseDto(Board board);

  List<BoardResponseDto> toResponseDtoList(List<Board> boards);

  @Mapping(target = "owners", ignore = true)
  @Mapping(target = "members", ignore = true)
  @Mapping(target = "boardItems", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "skills", ignore = true)
  Board toEntity(BoardCreateRequestDto createDto);

  @Mapping(target = "owners", ignore = true)
  @Mapping(target = "members", ignore = true)
  @Mapping(target = "boardItems", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "skills", ignore = true)
  void updateEntityFromDto(BoardUpdateRequestDto updateDto, @MappingTarget Board board);

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