package org.dljl.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    int conflictCount = appointmentMapper.checkCreateTimeConflict(
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
  public String createBlock(String startTimeStr, String endTimeStr, Long providerID) {
    // Parse the start and end time strings into LocalTime objects
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
    LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

    // Starting date (today)
    LocalDate startDate = LocalDate.now();
    // Date one year from now
    LocalDate endDate = startDate.plus(1, ChronoUnit.YEARS);

    // Loop through each day for the next year
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        // Combine date with start and end times to create LocalDateTime instances
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        Appointment appointment = new Appointment();
        appointment.setProviderId(providerID);
        appointment.setUserId(null);
        appointment.setStartDateTime(startDateTime);
        appointment.setEndDateTime(startDateTime);
        appointment.setStatus("blocked");
        appointment.setServiceType("blocked");
        appointment.setComments("blocked");
        //add this block into db
        appointmentMapper.createAppointment(appointment);

    }
    return "the block of "+ startTimeStr + "to" + endTimeStr + "has been created for the following one year";
  }

  @Override
  public Appointment updateAppointment(UpdateAppointmentDTO appointmentDTO) {
    int conflictCount = appointmentMapper.checkUpdateTimeConflict(
            appointmentDTO.getAppointmentId(),
            appointmentDTO.getStartDateTime().truncatedTo(ChronoUnit.SECONDS),
            appointmentDTO.getEndDateTime().truncatedTo(ChronoUnit.SECONDS)
    );

    if (conflictCount != 0) {
      throw new IllegalArgumentException("The selected time slot conflicts with an existing appointment.");
    }

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
  public List<Appointment> getAppointmentsByProviderAndDate(Long providerId, LocalDate appointmentDate) {
    return appointmentMapper.getAppointmentsByProviderAndDate(providerId, appointmentDate);
  }

  @Override
  public boolean cancelAppointment(Long id) {
    // Call the mapper to cancel the appointment
    int rowsAffected = appointmentMapper.cancelAppointment(id);

    // If rowsAffected is 1, the appointment was successfully cancelled; otherwise, it was not found
    return rowsAffected == 1;
  }

  @Override
  public List<List<LocalDateTime>> getAvailableTimeIntervals(Long providerId, LocalDate date) {

    List<Appointment> appointments = appointmentMapper.getAppointmentsByProviderAndDate(providerId, date);

    LocalDateTime dayStart = date.atStartOfDay();
    LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

    List<List<LocalDateTime>> availableTimeIntervals = new ArrayList<>();

    if (appointments.isEmpty()) {
      List<LocalDateTime> fullDay = new ArrayList<>();
      fullDay.add(dayStart);
      fullDay.add(dayEnd);
      availableTimeIntervals.add(fullDay);
      return availableTimeIntervals;
    }

    appointments.sort((a, b) -> a.getStartDateTime().compareTo(b.getStartDateTime()));

    LocalDateTime currentStart = dayStart;

    for (Appointment appointment : appointments) {
      LocalDateTime appointmentStart = appointment.getStartDateTime();
      LocalDateTime appointmentEnd = appointment.getEndDateTime();

      if (currentStart.isBefore(appointmentStart)) {
        List<LocalDateTime> availableInterval = new ArrayList<>();
        availableInterval.add(currentStart);
        availableInterval.add(appointmentStart);
        availableTimeIntervals.add(availableInterval);
      }

      currentStart = appointmentEnd.isAfter(currentStart) ? appointmentEnd : currentStart;
    }

    if (currentStart.isBefore(dayEnd)) {
      List<LocalDateTime> availableInterval = new ArrayList<>();
      availableInterval.add(currentStart);
      availableInterval.add(dayEnd);
      availableTimeIntervals.add(availableInterval);
    }

    return availableTimeIntervals;
  }

  @Override
  public List<Appointment> getAppointmentHistory(Long providerId, Long userId) {
    return appointmentMapper.findAppointmentsByProviderAndUser(providerId, userId);
  }
}
