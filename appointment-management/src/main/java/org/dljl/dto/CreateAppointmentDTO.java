package org.dljl.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentDTO {
  private Long providerId;
  private Long userId;
  private LocalDateTime appointmentDateTime;
  private String status;
  private String serviceType;
  private String comments;
}
