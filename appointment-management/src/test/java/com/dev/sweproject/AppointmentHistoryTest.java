package com.dev.sweproject;

import org.dljl.AppointmentManagementService;
import org.dljl.controller.AppointmentController;
import org.dljl.service.AppointmentService;
import org.dljl.entity.Appointment;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AppointmentManagementService.class)
public class AppointmentHistoryTest {

  @InjectMocks
  private AppointmentController appointmentController;

  @Mock
  private AppointmentService appointmentService; // Mocking the service layer

  @Test
  public void testGetAppointmentHistory() {
    // Creating mock data
    Appointment appointment1 = new Appointment();
    appointment1.setAppointmentId(1L);
    appointment1.setAppointmentDateTime(LocalDateTime.now().minusDays(1));
    appointment1.setStatus("completed");
    appointment1.setServiceType("Consultation");
    appointment1.setComments("First appointment.");

    Appointment appointment2 = new Appointment();
    appointment2.setAppointmentId(2L);
    appointment2.setAppointmentDateTime(LocalDateTime.now().minusDays(2));
    appointment2.setStatus("canceled");
    appointment2.setServiceType("Repair");
    appointment2.setComments("Client canceled.");

    List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);

    // Mocking the service call
    when(appointmentService.getAppointmentHistory(1L, 1L)).thenReturn(mockAppointments);

    // Calling the controller method
    ResponseEntity<List<Map<String, Object>>> response = appointmentController.getAppointmentHistory(1L, 1L);

    // Validating the response
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(2, response.getBody().size());
    assertEquals("completed", response.getBody().get(0).get("Status"));
    assertEquals("canceled", response.getBody().get(1).get("Status"));
  }
}
