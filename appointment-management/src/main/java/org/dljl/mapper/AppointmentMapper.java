package org.dljl.mapper;

import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.entity.Appointment;
import org.apache.ibatis.annotations.*;

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

  // Update the appointment using UpdateAppointmentDTO
  void updateAppointment(UpdateAppointmentDTO appointmentDTO);

  // Get the number of conflicted appointments (maximum 1)
  @Select("SELECT COUNT(*) " +
          "FROM appointments " +
          "WHERE provider_id = #{providerId} " +
          "AND " +
          "((#{startDateTime} < end_date_time AND #{startDateTime} >= start_date_time) " +
          "OR (#{endDateTime} <= end_date_time AND #{endDateTime} > start_date_time) " +
          "OR (#{startDateTime} <= start_date_time AND #{endDateTime} >= end_date_time))")
  int checkTimeConflict(@Param("providerId") Long providerId,
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);

  @Select("SELECT appointment_id, start_date_time, end_date_time, status, service_type, comments " +
    "FROM appointments " +
    "WHERE provider_id = #{providerId} AND user_id = #{userId}")
  List<Appointment> findAppointmentsByProviderAndUser(@Param("providerId") Long providerId, @Param("userId") Long userId);
}
