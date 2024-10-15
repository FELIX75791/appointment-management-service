package org.dljl.mapper;

import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.entity.Appointment;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface AppointmentMapper {

  // Create a new appointment and retrieve the auto-generated appointmentId
  void createAppointment(Appointment appointment);

  // Get an appointment by its ID
  Appointment getAppointment(Long id);

  // Cancel an appointment by setting its status to 'cancelled', return number of rows affected
  int cancelAppointment(Long id);

  // Get all appointments by the provider ID
  List<Appointment> getAppointmentsByProviderId(Long providerId);

  // Get all appointments by the provider ID and Date
  List<Appointment> getAppointmentsByProviderAndDate(Long providerId, LocalDate appointmentDate);

  // Update the appointment using UpdateAppointmentDTO
  void updateAppointment(UpdateAppointmentDTO appointmentDTO);

  // Get the number of conflicted appointments (maximum 1)
  int checkCreateTimeConflict(Long providerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

  // Get the number of conflicted appointments (maximum 1)
  int checkUpdateTimeConflict(Long appointmentId, LocalDateTime startDateTime, LocalDateTime endDateTime);

  //Get all history by given provider and user
  List<Appointment> findAppointmentsByProviderAndUser( Long providerId, Long userId);
}
