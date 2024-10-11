package org.dljl.service;

import java.util.List;
import org.dljl.entity.Appointment;

public interface AppointmentService {

  Appointment createAppointment(Appointment appointment);

  Appointment getAppointmentById(Long id);

  List<Appointment> getAppointmentsByProviderId(Long providerId);

  Appointment updateAppointment(Appointment appointment);

  void deleteAppointment(Long id);

  List<Appointment> getAppointmentHistory(Long providerId, Long userId);
}
