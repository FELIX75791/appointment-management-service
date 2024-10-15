package org.dljl.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBlockDTO {

  private Long providerId;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;

}