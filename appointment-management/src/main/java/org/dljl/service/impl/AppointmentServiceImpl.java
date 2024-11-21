package org.dljl.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.dljl.dto.CreateAppointmentDto;
import org.dljl.dto.CreateBlockDto;
import org.dljl.dto.CreateRecurringBlockInOneYearDto;
import org.dljl.dto.UpdateAppointmentDto;
import org.dljl.entity.Appointment;
import org.dljl.mapper.AppointmentMapper;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** The type Appointment service. */
@Service
public class AppointmentServiceImpl implements AppointmentService {

  @Autowired private AppointmentMapper appointmentMapper;

  @Override
  public Appointment createAppointment(CreateAppointmentDto appointmentDto) {

    int conflictCount =
        appointmentMapper.checkCreateTimeConflict(
            appointmentDto.getProviderId(),
            appointmentDto.getStartDateTime().truncatedTo(ChronoUnit.SECONDS),
            appointmentDto.getEndDateTime().truncatedTo(ChronoUnit.SECONDS));

    if (conflictCount != 0) {
      throw new IllegalArgumentException(
          "The selected time slot is not available or conflicts with an existing appointment.");
    }

    Appointment appointment = new Appointment();
    appointment.setProviderId(appointmentDto.getProviderId());
    appointment.setUserId(appointmentDto.getUserId());
    appointment.setStartDateTime(appointmentDto.getStartDateTime());
    appointment.setEndDateTime(appointmentDto.getEndDateTime());
    appointment.setStatus(appointmentDto.getStatus());
    appointment.setServiceType(appointmentDto.getServiceType());
    appointment.setComments(appointmentDto.getComments());

    appointmentMapper.createAppointment(appointment);
    return appointment;
  }

  @Override
  public String createBlock(CreateBlockDto blockDto) {
    // Check if provider_id is null before proceeding
    if (blockDto.getProviderId() == null) {
      throw new IllegalArgumentException("Provider ID Can't be null.");
    }
    Long providerId = blockDto.getProviderId();
    LocalDateTime startDateTime = blockDto.getStartDateTime();
    LocalDateTime endDateTime = blockDto.getEndDateTime();

    Appointment appointment = new Appointment();
    appointment.setProviderId(providerId); // Ensure providerId is set
    appointment.setUserId(null);
    appointment.setStartDateTime(startDateTime);
    appointment.setEndDateTime(endDateTime);
    appointment.setStatus("blocked");
    appointment.setServiceType("blocked");
    appointment.setComments("blocked");

    int conflictCount =
        appointmentMapper.checkCreateTimeConflict(
            providerId,
            startDateTime.truncatedTo(ChronoUnit.SECONDS),
            endDateTime.truncatedTo(ChronoUnit.SECONDS));

    if (conflictCount != 0) {
      throw new IllegalArgumentException(
          "The selected time slot is not available or conflicts with an existing appointment. "
              + "To block this time, please cancel the conflicting appointment or block.");
    }
    appointmentMapper.createAppointment(appointment);

    return "Block Created Successfully";
  }

  @Override
  public String createRecurringBlockInOneYear(CreateRecurringBlockInOneYearDto blockDto) {

    if (blockDto.getProviderId() == null) {
      throw new IllegalArgumentException("Provider ID Can't be null.");
    }

    Long providerId = blockDto.getProviderId();
    LocalTime startTime = blockDto.getStartTime();
    LocalTime endTime = blockDto.getEndTime();
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusYears(1);

    StringBuilder conflictDates = new StringBuilder();
    boolean hasConflict = false;

    // Step 1: Check for conflicts across the entire year
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
      LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

      CreateBlockDto singleDayBlockDto = new CreateBlockDto();
      singleDayBlockDto.setProviderId(providerId);
      singleDayBlockDto.setStartDateTime(startDateTime);
      singleDayBlockDto.setEndDateTime(endDateTime);

      try {
        createBlock(singleDayBlockDto);
      } catch (IllegalArgumentException e) {
        hasConflict = true;
        conflictDates.append(date).append("\n");
      }
    }

    // Step 2: Return result based on whether conflicts were found
    if (hasConflict) {
      return "Conflicts found on the following dates: \n" + conflictDates.toString();
    } else {
      return "Yearly recurring block created successfully.";
    }
  }

  @Override
  public Appointment updateAppointment(UpdateAppointmentDto appointmentDto) {

    if (appointmentDto.getAppointmentId() == null) {
      throw new IllegalArgumentException("Appointment ID is required for updating an appointment.");
    }
    if (appointmentDto.getStartDateTime() != null || appointmentDto.getEndDateTime() != null) {
      Appointment originalAppointment = getAppointment(appointmentDto.getAppointmentId());
      if (appointmentDto.getStartDateTime() == null) {
        appointmentDto.setStartDateTime(originalAppointment.getStartDateTime());
      }
      if (appointmentDto.getEndDateTime() == null) {
        appointmentDto.setEndDateTime(originalAppointment.getEndDateTime());
      }

      int conflictCount =
          appointmentMapper.checkUpdateTimeConflict(
              appointmentDto.getAppointmentId(),
              appointmentDto.getStartDateTime().truncatedTo(ChronoUnit.SECONDS),
              appointmentDto.getEndDateTime().truncatedTo(ChronoUnit.SECONDS));

      if (conflictCount != 0) {
        throw new IllegalArgumentException(
            "The updated time slot conflicts with an existing appointment or blocked time.");
      }
    }

    appointmentMapper.updateAppointment(appointmentDto);

    return appointmentMapper.getAppointment(appointmentDto.getAppointmentId());
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
  public List<Appointment> getAppointmentsByProviderAndDate(
      Long providerId, LocalDate appointmentDate) {
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
  public boolean deleteBlock(Long id) {
    // Call the mapper to cancel the appointment
    int rowsAffected = appointmentMapper.deleteBlock(id);

    // If rowsAffected is 1, the appointment was successfully cancelled; otherwise, it was not found
    return rowsAffected == 1;
  }

  @Override
  public List<List<LocalDateTime>> getAvailableTimeIntervals(Long providerId, LocalDate date) {

    List<Appointment> appointments =
        appointmentMapper.getAppointmentsByProviderAndDate(providerId, date);

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

  @Override
  public List<Appointment> getAppointmentsWithinDateRange(Long providerId, LocalDate startDate, LocalDate endDate) {
    if (providerId == null) {
      throw new IllegalArgumentException("Provider ID cannot be null.");
    }
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date cannot be null.");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date.");
    }

    return appointmentMapper.getAppointmentsWithinDateRange(providerId, startDate, endDate);
  }
}
