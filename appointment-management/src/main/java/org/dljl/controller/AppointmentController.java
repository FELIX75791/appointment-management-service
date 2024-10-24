package org.dljl.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.dljl.dto.CreateAppointmentDto;
import org.dljl.dto.CreateBlockDto;
import org.dljl.dto.CreateRecurringBlockInOneYearDto;
import org.dljl.dto.UpdateAppointmentDto;
import org.dljl.entity.Appointment;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** The type Appointment controller. */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

  @Autowired private AppointmentService appointmentService;

  /**
   * Create appointment response entity.
   *
   * @param appointmentDto the appointment dto
   * @return the response entity
   */
  // Create a new appointment
  @PostMapping("/createAppointment")
  public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentDto appointmentDto) {
    try {
      Appointment createdAppointment = appointmentService.createAppointment(appointmentDto);
      return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Create block response entity.
   *
   * @param blockDto the block dto
   * @return the response entity
   */
  // Create recurring block in one year
  @PostMapping("/createBlock")
  public ResponseEntity<String> createBlock(@RequestBody CreateBlockDto blockDto) {
    try {
      String result = appointmentService.createBlock(blockDto);
      return new ResponseEntity<>(result, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Create recurring block in one year response entity.
   *
   * @param blockDto the block dto
   * @return the response entity
   */
  // Create a recurring block for one year
  @PostMapping("/createRecurringBlockInOneYear")
  public ResponseEntity<String> createRecurringBlockInOneYear(
      @RequestBody CreateRecurringBlockInOneYearDto blockDto) {
    try {
      String result = appointmentService.createRecurringBlockInOneYear(blockDto);
      return new ResponseEntity<>(result, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update appointment response entity.
   *
   * @param appointmentDto the appointment dto
   * @return the response entity
   */
  // Update an appointment
  @PutMapping("/update")
  public ResponseEntity<?> updateAppointment(@RequestBody UpdateAppointmentDto appointmentDto) {
    try {
      Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDto);
      return ResponseEntity.ok(updatedAppointment);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Cancel appointment response entity.
   *
   * @param id the id
   * @return the response entity
   */
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

  /**
   * Delete a block entity. This will permanently delete a block from database to avoid unnecessary
   * storage usage. This can potentially be used on appointment as well but do so will result in
   * loss of user history. We did not enforce this API to be used on block only to give developer
   * full control, but use carefully.
   *
   * @param id the block id
   * @return the response entity
   */
  // Cancel an appointment
  @DeleteMapping("/deleteBlock/{id}")
  public ResponseEntity<String> deleteBlock(@PathVariable Long id) {
    boolean isCancelled = appointmentService.deleteBlock(id);
    if (isCancelled) {
      return ResponseEntity.ok("Block cancelled successfully.");
    } else {
      return ResponseEntity.badRequest().body("Block not found or already deleted.");
    }
  }

  /**
   * Gets appointment.
   *
   * @param id the id
   * @return the appointment
   */
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

  /**
   * Gets appointments by provider id.
   *
   * @param providerId the provider id
   * @return the appointments by provider id
   */
  // Get appointments by provider ID
  @GetMapping("/provider/{providerId}")
  public ResponseEntity<List<Appointment>> getAppointmentsByProviderId(
      @PathVariable Long providerId) {
    List<Appointment> appointments = appointmentService.getAppointmentsByProviderId(providerId);
    return ResponseEntity.ok(appointments);
  }

  /**
   * Gets appointments by provider and date.
   *
   * @param providerId the provider id
   * @param appointmentDate the appointment date
   * @return the appointments by provider and date
   */
  // Get appointments by provider ID and Date
  @GetMapping("/provider/{providerId}/date/{appointmentDate}")
  public ResponseEntity<List<Appointment>> getAppointmentsByProviderAndDate(
      @PathVariable("providerId") Long providerId,
      @PathVariable("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate appointmentDate) {
    List<Appointment> appointments =
        appointmentService.getAppointmentsByProviderAndDate(providerId, appointmentDate);
    return ResponseEntity.ok(appointments);
  }

  /**
   * Gets available time intervals.
   *
   * @param providerId the provider id
   * @param appointmentDate the appointment date
   * @return the available time intervals
   */
  // Get all available time intervals in a day by provider ID and Date
  @GetMapping("/provider/{providerId}/available/date/{appointmentDate}")
  public ResponseEntity<List<List<LocalDateTime>>> getAvailableTimeIntervals(
      @PathVariable("providerId") Long providerId,
      @PathVariable("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate appointmentDate) {
    List<List<LocalDateTime>> availableTimeIntervals =
        appointmentService.getAvailableTimeIntervals(providerId, appointmentDate);
    return ResponseEntity.ok(availableTimeIntervals);
  }

  /**
   * Gets appointment history.
   *
   * @param providerId the provider id
   * @param userId the user id
   * @return the appointment history
   */
  @GetMapping("/history")
  public ResponseEntity<List<Map<String, Object>>> getAppointmentHistory(
      @RequestParam("provider_id") Long providerId, @RequestParam("user_id") Long userId) {

    List<Appointment> appointmentHistory =
        appointmentService.getAppointmentHistory(providerId, userId);

    if (appointmentHistory.isEmpty()) {
      // Add a custom message to the response list
      Map<String, Object> message = new HashMap<>();
      message.put("message", "No appointment history found for the given provider and user.");
      List<Map<String, Object>> response = new ArrayList<>();
      response.add(message);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Customize the output
    List<Map<String, Object>> response =
        appointmentHistory.stream()
            .map(
                appointment -> {
                  Map<String, Object> appointmentDetails = new HashMap<>();
                  appointmentDetails.put("Appointment ID", appointment.getAppointmentId());
                  appointmentDetails.put("Start Date and Time", appointment.getStartDateTime());
                  appointmentDetails.put("End Date and Time", appointment.getEndDateTime());
                  appointmentDetails.put("Status", appointment.getStatus());
                  appointmentDetails.put("Service Type", appointment.getServiceType());
                  appointmentDetails.put("Comments", appointment.getComments());
                  return appointmentDetails;
                })
            .collect(Collectors.toList());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
