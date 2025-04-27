package com.saunderscox.taskolotl.dto;

import java.util.List;
import java.util.UUID;

public class UserResponseDto extends BaseResponseDto {

  private String profileDescription;
  private String profilePicture;
  private List<String> skills;
  private List<String> roles;
  private List<UUID> friendList;
}