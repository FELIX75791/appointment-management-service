package org.dljl.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** The type Update appointment dto. */
@Getter
@Setter
public class UpdateAppointmentDto {

  private Long appointmentId; // Required
  private Long userId; // Optional
  private LocalDateTime startDateTime; // Optional
  private LocalDateTime endDateTime; // Optional
  private String status; // Optional
  private String serviceType; // Optional
  private String comments; // Optional
}
