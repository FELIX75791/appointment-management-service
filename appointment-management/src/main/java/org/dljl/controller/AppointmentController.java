package org.dljl.controller;

import java.util.List;
import org.dljl.entity.Appointment;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

  @Autowired
  private AppointmentService appointmentService;

  @PostMapping
  public Appointment createAppointment(@RequestBody Appointment appointment) {
    return appointmentService.createAppointment(appointment);
  }

  @GetMapping("/{id}")
  public Appointment getAppointmentById(@PathVariable Long id) {
    return appointmentService.getAppointmentById(id);
  }

  @GetMapping("/provider/{providerId}")
  public List<Appointment> getAppointmentsByProvider(@PathVariable Long providerId) {
    return appointmentService.getAppointmentsByProviderId(providerId);
  }

  @PutMapping("/{id}")
  public Appointment updateAppointment(@PathVariable Long id,
      @RequestBody Appointment appointment) {
    appointment.setId(id);
    return appointmentService.updateAppointment(appointment);
  }

  @DeleteMapping("/{id}")
  public void deleteAppointment(@PathVariable Long id) {
    appointmentService.deleteAppointment(id);
  }
}
