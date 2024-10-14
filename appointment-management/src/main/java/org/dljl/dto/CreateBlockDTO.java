package org.dljl.dto;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBlockDTO {
  private Long providerId;
  private LocalTime startTime;
  private LocalTime endTime;

  // Getters and setters
  public Long getProviderId() {
      return providerId;
  }
  
  public void setProviderId(Long providerId) {
      this.providerId = providerId;
  }
  
  public LocalTime getStartTime() {
      return startTime;
  }
  
  public void setStartTime(LocalTime startTime) {
      this.startTime = startTime;
  }
  
  public LocalTime getEndTime() {
      return endTime;
  }
  
  public void setEndTime(LocalTime endTime) {
      this.endTime = endTime;
  }
}
