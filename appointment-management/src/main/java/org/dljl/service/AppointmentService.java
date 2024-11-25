package org.dljl.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.dljl.dto.CreateAppointmentDto;
import org.dljl.dto.CreateBlockDto;
import org.dljl.dto.CreateRecurringBlockDto;
import org.dljl.dto.CreateRecurringBlockInOneYearDto;
import org.dljl.dto.UpdateAppointmentDto;
import org.dljl.entity.Appointment;

/** The interface Appointment service. */
public interface AppointmentService {

  /**
   * Create appointment appointment.
   *
   * @param appointmentDto the appointment dto
   * @return the appointment
   */
  Appointment createAppointment(CreateAppointmentDto appointmentDto);

  /**
   * Create block string.
   *
   * @param blockDto the block dto
   * @return the string
   */
  String createBlock(CreateBlockDto blockDto);

  /**
   * Create recurring block in one year string.
   *
   * @param blockDto the block dto
   * @return the string
   */
  String createRecurringBlockInOneYear(CreateRecurringBlockInOneYearDto blockDto);

  /**
   * Update appointment appointment.
   *
   * @param appointmentDto the appointment dto
   * @return the appointment
   */
  Appointment updateAppointment(UpdateAppointmentDto appointmentDto);

  /**
   * Cancel appointment.
   *
   * @param id the id
   * @return the boolean
   */
  boolean cancelAppointment(Long id);

  /**
   * Delete block.
   *
   * @param id the id
   * @return the boolean whether block is deleted.
   */
  boolean deleteBlock(Long id);

  /**
   * Gets appointment.
   *
   * @param id the id
   * @return the appointment
   */
  Appointment getAppointment(Long id);

  /**
   * Gets appointments by provider id.
   *
   * @param providerId the provider id
   * @return the appointments by provider id
   */
  List<Appointment> getAppointmentsByProviderId(Long providerId);

  /**
   * Gets appointments by provider and date.
   *
   * @param providerId the provider id
   * @param appointmentDate the appointment date
   * @return the appointments by provider and date
   */
  List<Appointment> getAppointmentsByProviderAndDate(Long providerId, LocalDate appointmentDate);

  /**
   * Gets available time intervals.
   *
   * @param providerId the provider id
   * @param date the date
   * @return the available time intervals
   */
  List<List<LocalDateTime>> getAvailableTimeIntervals(Long providerId, LocalDate date);

  /**
   * Gets appointment history.
   *
   * @param providerId the provider id
   * @param userId the user id
   * @return the appointment history
   */
  List<Appointment> getAppointmentHistory(Long providerId, Long userId);

  /**
   * Gets appointments within a date range for a provider.
   *
   * @param providerId the provider id
   * @param startDate the start date
   * @param endDate the end date
   * @return the list of appointments within the specified date range
   */
  List<Appointment> getAppointmentsWithinDateRange(Long providerId, LocalDate startDate, LocalDate endDate);

    /**
   * Create recurring block.
   *
   * @param blockDto the block dto
   * @return the string
   */
  String createRecurringBlock(CreateRecurringBlockDto blockDto);
}
