package org.dljl.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** The type Create block dto. */
@Getter
@Setter
public class CreateBlockDto {

  private Long providerId;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
}
