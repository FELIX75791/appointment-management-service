package org.dljl.mapper;

import org.dljl.entity.Appointment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AppointmentMapper {

  @Insert("INSERT INTO appointments (provider_id, user_id, description, appointment_date_time, status) " +
      "VALUES (#{providerId}, #{userId}, #{description}, #{appointmentDateTime}, #{status})")
  void createAppointment(Appointment appointment);

  @Select("SELECT * FROM appointments WHERE id = #{id}")
  Appointment getAppointmentById(Long id);

  @Update("UPDATE appointments SET description=#{description}, appointment_date_time=#{appointmentDateTime}, status=#{status} WHERE id=#{id}")
  void updateAppointment(Appointment appointment);

  @Delete("DELETE FROM appointments WHERE id = #{id}")
  void deleteAppointment(Long id);

  @Select("SELECT * FROM appointments WHERE provider_id = #{providerId}")
  List<Appointment> getAppointmentsByProviderId(Long providerId);
}
