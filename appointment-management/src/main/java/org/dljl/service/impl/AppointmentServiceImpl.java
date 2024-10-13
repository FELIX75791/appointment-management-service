package org.dljl.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.dljl.dto.CreateAppointmentDTO;
import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.entity.Appointment;
import org.dljl.mapper.AppointmentMapper;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class AppointmentServiceImpl implements AppointmentService {

  @Autowired
  private AppointmentMapper appointmentMapper;

  @Override
  public Appointment createAppointment(CreateAppointmentDTO appointmentDTO) {

    int conflictCount = appointmentMapper.checkTimeConflict(
            appointmentDTO.getProviderId(),
            appointmentDTO.getStartDateTime().truncatedTo(ChronoUnit.SECONDS),
            appointmentDTO.getEndDateTime().truncatedTo(ChronoUnit.SECONDS)
    );

    if (conflictCount != 0) {
      throw new IllegalArgumentException("The selected time slot conflicts with an existing appointment.");
    }

    Appointment appointment = new Appointment();
    appointment.setProviderId(appointmentDTO.getProviderId());
    appointment.setUserId(appointmentDTO.getUserId());
    appointment.setStartDateTime(appointmentDTO.getStartDateTime());
    appointment.setEndDateTime(appointmentDTO.getEndDateTime());
    appointment.setStatus(appointmentDTO.getStatus());
    appointment.setServiceType(appointmentDTO.getServiceType());
    appointment.setComments(appointmentDTO.getComments());

    appointmentMapper.createAppointment(appointment);
    return appointment;
  }

  @Override
  public Appointment updateAppointment(UpdateAppointmentDTO appointmentDTO) {
    // Ensure the appointmentId is provided, as it's required for the update
    if (appointmentDTO.getAppointmentId() == null) {
      throw new IllegalArgumentException("Appointment ID is required for updating an appointment.");
    }

    // Perform the update using the DTO
    appointmentMapper.updateAppointment(appointmentDTO);

    // Retrieve the updated appointment from the database

    // Return the updated appointment
    return appointmentMapper.getAppointment(
        appointmentDTO.getAppointmentId());
  }

  @Override
  public Appointment getAppointment(Long id) {
    return appointmentMapper.getAppointment(id);
  }

  @Override
  public List<Appointment> getAppointmentsByProviderId(Long providerId) {
    return appointmentMapper.getAppointmentsByProviderId(providerId);
  }

  @Override
  public boolean cancelAppointment(Long id) {
    // Call the mapper to cancel the appointment
    int rowsAffected = appointmentMapper.cancelAppointment(id);

    // If rowsAffected is 1, the appointment was successfully cancelled; otherwise, it was not found
    return rowsAffected == 1;
  }

  @Override
  public List<Appointment> getAppointmentHistory(Long providerId, Long userId) {
    return appointmentMapper.findAppointmentsByProviderAndUser(providerId, userId);
  }
}
