package org.dljl.service;

import java.util.List;
import org.dljl.dto.CreateAppointmentDTO;
import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.entity.Appointment;

public interface AppointmentService {

  Appointment createAppointment(CreateAppointmentDTO appointmentDTO);

  String createBlock(String startTimeStr, String endTimeStr, Long providerID);

  Appointment updateAppointment(UpdateAppointmentDTO appointmentDTO);

  boolean cancelAppointment(Long id);

  Appointment getAppointment(Long id);

  List<Appointment> getAppointmentsByProviderId(Long providerId);

  List<Appointment> getAppointmentHistory(Long providerId, Long userId);
}
