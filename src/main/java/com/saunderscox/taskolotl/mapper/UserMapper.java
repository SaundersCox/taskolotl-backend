package com.saunderscox.taskolotl.mapper;

import com.saunderscox.taskolotl.dto.UserCreateRequest;
import com.saunderscox.taskolotl.dto.UserResponse;
import com.saunderscox.taskolotl.dto.UserUpdateRequest;
import com.saunderscox.taskolotl.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Maps between User entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

  UserResponse toResponseDto(User user);

  List<UserResponse> toResponseDtoList(List<User> users);

  User toEntity(UserCreateRequest createDto);

  void updateEntityFromDto(UserUpdateRequest updateDto, @MappingTarget User user);
}
