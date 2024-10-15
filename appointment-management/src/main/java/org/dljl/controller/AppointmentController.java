package org.dljl.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.dljl.dto.CreateAppointmentDTO;
import org.dljl.dto.CreateBlockDTO;
import org.dljl.dto.CreateRecurringBlockInOneYearDTO;
import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.entity.Appointment;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {

  @Autowired
  private AppointmentService appointmentService;

  // Create a new appointment
  @PostMapping("/createAppointment")
  public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentDTO appointmentDTO) {
    try {
      Appointment createdAppointment = appointmentService.createAppointment(appointmentDTO);
      return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  // Create recurring block in one year
  @PostMapping("/createBlock")
  public ResponseEntity<String> createBlock(@RequestBody CreateBlockDTO blockDTO) {
    try {
      String result = appointmentService.createBlock(blockDTO);
      return new ResponseEntity<>(result, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Create a recurring block for one year
  @PostMapping("/createRecurringBlockInOneYear")
  public ResponseEntity<String> createRecurringBlockInOneYear(
      @RequestBody CreateRecurringBlockInOneYearDTO blockDTO) {
    try {
      String result = appointmentService.createRecurringBlockInOneYear(blockDTO);
      return new ResponseEntity<>(result, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Update an appointment
  @PutMapping("/update")
  public ResponseEntity<Appointment> updateAppointment(
      @RequestBody UpdateAppointmentDTO appointmentDTO) {
    try {
      Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);
      return ResponseEntity.ok(updatedAppointment);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    }
  }

  // Cancel an appointment
  @PutMapping("/cancel/{id}")
  public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
    boolean isCancelled = appointmentService.cancelAppointment(id);
    if (isCancelled) {
      return ResponseEntity.ok("Appointment cancelled successfully.");
    } else {
      return ResponseEntity.badRequest().body("Appointment not found or already cancelled.");
    }
  }

  // Get an appointment by ID
  @GetMapping("/{id}")
  public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
    Appointment appointment = appointmentService.getAppointment(id);
    if (appointment != null) {
      return ResponseEntity.ok(appointment);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  // Get appointments by provider ID
  @GetMapping("/provider/{providerId}")
  public ResponseEntity<List<Appointment>> getAppointmentsByProviderId(
      @PathVariable Long providerId) {
    List<Appointment> appointments = appointmentService.getAppointmentsByProviderId(providerId);
    return ResponseEntity.ok(appointments);
  }

  // Get appointments by provider ID and Date
  @GetMapping("/provider/{providerId}/date/{appointmentDate}")
  public ResponseEntity<List<Appointment>> getAppointmentsByProviderAndDate(
      @PathVariable("providerId") Long providerId,
      @PathVariable("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {
    List<Appointment> appointments = appointmentService.getAppointmentsByProviderAndDate(providerId,
        appointmentDate);
    return ResponseEntity.ok(appointments);
  }

  // Get all available time intervals in a day by provider ID and Date
  @GetMapping("/provider/{providerId}/available/date/{appointmentDate}")
  public ResponseEntity<List<List<LocalDateTime>>> getAvailableTimeIntervals(
      @PathVariable("providerId") Long providerId,
      @PathVariable("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {
    List<List<LocalDateTime>> availableTimeIntervals = appointmentService.getAvailableTimeIntervals(
        providerId, appointmentDate);
    return ResponseEntity.ok(availableTimeIntervals);
  }

  @GetMapping("/history")
  public ResponseEntity<List<Map<String, Object>>> getAppointmentHistory(
      @RequestParam("provider_id") Long providerId, @RequestParam("user_id") Long userId) {

    List<Appointment> appointmentHistory = appointmentService.getAppointmentHistory(providerId,
        userId);

    if (appointmentHistory.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Customize the output
    List<Map<String, Object>> response = appointmentHistory.stream().map(appointment -> {
      Map<String, Object> appointmentDetails = new HashMap<>();
      appointmentDetails.put("Appointment ID", appointment.getAppointmentId());
      appointmentDetails.put("Start Date and Time", appointment.getStartDateTime());
      appointmentDetails.put("End Date and Time", appointment.getEndDateTime());
      appointmentDetails.put("Status", appointment.getStatus());
      appointmentDetails.put("Service Type", appointment.getServiceType());
      appointmentDetails.put("Comments", appointment.getComments());
      return appointmentDetails;
    }).collect(Collectors.toList());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
