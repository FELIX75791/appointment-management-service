package org.dljl.dto;

import java.time.LocalTime;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** The type Create recurring block dto. */
@Getter
@Setter
public class CreateRecurringBlockDto {

  private Long providerId;
  private LocalTime startTime;
  private LocalTime endTime;
  private LocalDate startDate;
  private LocalDate endDate;
  
}
