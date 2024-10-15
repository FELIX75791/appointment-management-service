package org.dljl.dto;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecurringBlockInOneYearDTO {

  private Long providerId;
  private LocalTime startTime;
  private LocalTime endTime;

}
