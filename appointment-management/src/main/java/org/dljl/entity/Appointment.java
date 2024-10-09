package org.dljl.entity;

import lombok.Getter;
import lombok.Setter;

public class Appointment {

  @Getter
  @Setter
  private Long id;
  private Long providerId;
  private Long userId;
  private String description;
  private String appointmentDateTime;
  private String status;

  // Getters and Setters
}
