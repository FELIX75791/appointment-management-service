package org.dljl.dto;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

/** The type Create recurring block in one year dto. */
@Getter
@Setter
public class CreateRecurringBlockInOneYearDto {

  private Long providerId;
  private LocalTime startTime;
  private LocalTime endTime;
}
