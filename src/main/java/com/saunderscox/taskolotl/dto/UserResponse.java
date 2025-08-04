package com.saunderscox.taskolotl.dto;

import com.saunderscox.taskolotl.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserResponse extends BaseResponse {

  private String username;
  private String email;
  private String profileDescription;
  private String profilePictureUrl;
  private String oauthProvider;
  private String oauthId;
  private String permission;
  private Team team;
}
