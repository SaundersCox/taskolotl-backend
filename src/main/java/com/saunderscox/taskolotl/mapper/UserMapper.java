package com.saunderscox.taskolotl.mapper;

import com.saunderscox.taskolotl.dto.UserCreateRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.dto.UserUpdateRequest;
import com.saunderscox.taskolotl.entity.BaseEntity;
import com.saunderscox.taskolotl.entity.User;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps between User entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

  @Mapping(target = "skillIds", expression = "java(getSkillIds(user))")
  @Mapping(target = "roleIds", expression = "java(getRoleIds(user))")
  UserResponse toResponseDto(User user);

  List<UserResponse> toResponseDtoList(List<User> users);

  User toEntity(UserCreateRequest createDto);

  void updateEntityFromDto(UserUpdateRequest updateDto, @MappingTarget User user);

  default Set<UUID> getSkillIds(User user) {
    return user.getSkills().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }

  default Set<UUID> getRoleIds(User user) {
    return user.getRoles().stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toSet());
  }
}
