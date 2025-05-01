package com.saunderscox.taskolotl.mapper;

import com.saunderscox.taskolotl.dto.UserCreateRequestDto;
import com.saunderscox.taskolotl.dto.UserResponseDto;
import com.saunderscox.taskolotl.dto.UserUpdateRequestDto;
import com.saunderscox.taskolotl.entity.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Maps between User entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

  @Mapping(target = "skillIds", expression = "java(getSkillIds(user))")
  @Mapping(target = "roleIds", expression = "java(getRoleIds(user))")
  UserResponseDto toResponseDto(User user);

  List<UserResponseDto> toResponseDtoList(List<User> users);

  User toEntity(UserCreateRequestDto createDto);

  void updateEntityFromDto(UserUpdateRequestDto updateDto, @MappingTarget User user);

  default Set<UUID> getSkillIds(User user) {
    return user.getSkills().stream()
        .map(skill -> skill.getId())
        .collect(Collectors.toSet());
  }

  default Set<UUID> getRoleIds(User user) {
    return user.getRoles().stream()
        .map(role -> role.getId())
        .collect(Collectors.toSet());
  }
}