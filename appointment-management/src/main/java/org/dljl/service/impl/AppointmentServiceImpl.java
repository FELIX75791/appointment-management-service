package org.dljl.service.impl;

import java.util.List;
import org.dljl.entity.Appointment;
import org.dljl.mapper.AppointmentMapper;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {

  @Autowired
  private AppointmentMapper appointmentMapper;

  @Override
  public Appointment createAppointment(Appointment appointment) {
    appointmentMapper.createAppointment(appointment);
    return appointment;
  }

  @Override
  public Appointment getAppointmentById(Long id) {
    return appointmentMapper.getAppointmentById(id);
  }

  @Override
  public List<Appointment> getAppointmentsByProviderId(Long providerId) {
    return appointmentMapper.getAppointmentsByProviderId(providerId);
  }

  @Override
  public Appointment updateAppointment(Appointment appointment) {
    appointmentMapper.updateAppointment(appointment);
    return appointment;
  }

  @Override
  public void deleteAppointment(Long id) {
    appointmentMapper.deleteAppointment(id);
  }
  @Override
  public List<Appointment> getAppointmentHistory(Long providerId, Long userId) {
    return appointmentMapper.findAppointmentsByProviderAndUser(providerId, userId);
  }
}
