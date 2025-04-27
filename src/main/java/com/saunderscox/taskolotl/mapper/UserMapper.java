package com.saunderscox.taskolotl.mapper;

import com.saunderscox.taskolotl.dto.UserRequestDto;
import com.saunderscox.taskolotl.dto.UserResponseDto;
import com.saunderscox.taskolotl.entity.User;

//@Mapper(componentModel = "spring")
public interface UserMapper {
  User requestDtoToEntity(UserRequestDto dto);
  UserResponseDto entityToResponseDto(User user);
}
