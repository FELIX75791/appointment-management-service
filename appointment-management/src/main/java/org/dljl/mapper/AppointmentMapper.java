package org.dljl.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.dljl.dto.UpdateAppointmentDto;
import org.dljl.entity.Appointment;

/** The interface Appointment mapper. */
@Mapper
public interface AppointmentMapper {

  /**
   * Create appointment.
   *
   * @param appointment the appointment
   */
  // Create a new appointment and retrieve the auto-generated appointmentId
  void createAppointment(Appointment appointment);

  /**
   * Gets appointment.
   *
   * @param id the id
   * @return the appointment
   */
  // Get an appointment by its ID
  Appointment getAppointment(Long id);

  /**
   * Cancel appointment int.
   *
   * @param id the id
   * @return the int
   */
  // Cancel an appointment by setting its status to 'cancelled', return number of rows affected
  int cancelAppointment(Long id);

  /**
   * Gets appointments by provider id.
   *
   * @param providerId the provider id
   * @return the appointments by provider id
   */
  // Get all appointments by the provider ID
  List<Appointment> getAppointmentsByProviderId(Long providerId);

  /**
   * Gets appointments by provider and date.
   *
   * @param providerId the provider id
   * @param appointmentDate the appointment date
   * @return the appointments by provider and date
   */
  // Get all appointments by the provider ID and Date
  List<Appointment> getAppointmentsByProviderAndDate(Long providerId, LocalDate appointmentDate);

  /**
   * Update appointment.
   *
   * @param appointmentDto the appointment dto
   */
  // Update the appointment using UpdateAppointmentDTO
  void updateAppointment(UpdateAppointmentDto appointmentDto);

  /**
   * Check create time conflict int.
   *
   * @param providerId the provider id
   * @param startDateTime the start date time
   * @param endDateTime the end date time
   * @return the int
   */
  // Get the number of conflicted appointments (maximum 1)
  int checkCreateTimeConflict(
      Long providerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Check update time conflict int.
   *
   * @param appointmentId the appointment id
   * @param startDateTime the start date time
   * @param endDateTime the end date time
   * @return the int
   */
  // Get the number of conflicted appointments (maximum 1)
  int checkUpdateTimeConflict(
      Long appointmentId, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Find appointments by provider and user list.
   *
   * @param providerId the provider id
   * @param userId the user id
   * @return the list
   */
  // Get all history by given provider and user
  List<Appointment> findAppointmentsByProviderAndUser(Long providerId, Long userId);

  /**
   * Delete block.
   *
   * @param id the id
   * @return num rows affected
   */
  // delete the block permanently
  int deleteBlock(Long id);

  /**
   * Gets appointments within a date range for a provider.
   *
   * @param providerId the provider id
   * @param startDate the start date
   * @param endDate the end date
   * @return the list of appointments within the specified date range
   */
  List<Appointment> getAppointmentsWithinDateRange(Long providerId, 
      LocalDate startDate, LocalDate endDate);
}
