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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;


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

  public ResponseEntity<List<Map<String, Object>>> getAppointmentHistory(
    @RequestParam("provider_id") Long providerId,
    @RequestParam("user_id") Long userId) {

    List<Appointment> appointmentHistory = appointmentService.getAppointmentHistory(providerId, userId);

    if (appointmentHistory.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
//    List<Appointment> appointmentHistory = new ArrayList<>();
//
//    // Simulating some appointments
//    Appointment appointment1 = new Appointment();
//    appointment1.setAppointmentId(1L);
//    appointment1.setAppointmentDateTime(LocalDateTime.now().minusDays(1));
//    appointment1.setStatus("completed");
//    appointment1.setServiceType("Consultation");
//    appointment1.setComments("First appointment.");
//
//    Appointment appointment2 = new Appointment();
//    appointment2.setAppointmentId(2L);
//    appointment2.setAppointmentDateTime(LocalDateTime.now().minusDays(2));
//    appointment2.setStatus("canceled");
//    appointment2.setServiceType("Repair");
//    appointment2.setComments("Client canceled.");
//
//    appointmentHistory.add(appointment1);
//    appointmentHistory.add(appointment2);

    // Customize the output
    List<Map<String, Object>> response = appointmentHistory.stream().map(appointment -> {
      Map<String, Object> appointmentDetails = new HashMap<>();
      appointmentDetails.put("Appointment ID", appointment.getAppointmentId());
      appointmentDetails.put("Date and Time", appointment.getAppointmentDateTime());
      appointmentDetails.put("Status", appointment.getStatus());
      appointmentDetails.put("Service Type", appointment.getServiceType());
      appointmentDetails.put("Comments", appointment.getComments());
      return appointmentDetails;
    }).collect(Collectors.toList());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
