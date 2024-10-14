package org.dljl.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.dljl.dto.CreateAppointmentDTO;
import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.dto.CreateBlockDTO;
import org.dljl.entity.Appointment;

public interface AppointmentService {

  Appointment createAppointment(CreateAppointmentDTO appointmentDTO);

  String createBlock(CreateBlockDTO blockDTO);

  Appointment updateAppointment(UpdateAppointmentDTO appointmentDTO);

  boolean cancelAppointment(Long id);

  Appointment getAppointment(Long id);

  List<Appointment> getAppointmentsByProviderId(Long providerId);

  List<Appointment> getAppointmentsByProviderAndDate(Long providerId, LocalDate appointmentDate);

  List<List<LocalDateTime>> getAvailableTimeIntervals(Long providerId, LocalDate date);

  List<Appointment> getAppointmentHistory(Long providerId, Long userId);
}
