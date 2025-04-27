package com.saunderscox.taskolotl.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public abstract class BaseResponseDto {

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
}