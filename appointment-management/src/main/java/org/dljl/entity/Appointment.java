package org.dljl.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

public class Appointment {

  @Getter
  @Setter
  private Long id;
  private Long providerId;
  private Long userId;
  private String description;
  private LocalDateTime appointmentDateTime;
  private String status;
  private String serviceType;
  private String comments;

  public Long getAppointmentId() {
    return id;
  }

  public void setAppointmentId(Long appointmentId) {
    this.id = appointmentId;
  }

  public LocalDateTime getAppointmentDateTime() {
    return appointmentDateTime;
  }

  public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
    this.appointmentDateTime = appointmentDateTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }
  // Getters and Setters
}
