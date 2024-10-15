package org.dljl.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** The type Create appointment dto. */
@Getter
@Setter
public class CreateAppointmentDto {
  private Long providerId;
  private Long userId;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String status;
  private String serviceType;
  private String comments;
}
