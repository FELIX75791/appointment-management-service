package org.dljl.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAppointmentDTO {

  private Long appointmentId;  // Required
  private Long userId;  // Optional
  private LocalDateTime appointmentDateTime;  // Optional
  private String status;  // Optional
  private String serviceType;  // Optional
  private String comments;  // Optional
}
