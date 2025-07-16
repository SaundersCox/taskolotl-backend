package com.saunderscox.taskolotl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

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
  private Set<UUID> skillIds;
  private Set<UUID> roleIds;
}
