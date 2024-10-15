package org.dljl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.dljl.dto.CreateAppointmentDTO;
import org.dljl.entity.Appointment;
import org.dljl.mapper.AppointmentMapper;
import org.dljl.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AppointmentServiceTest {

  @Mock
  private AppointmentMapper appointmentMapper;

  @InjectMocks
  private AppointmentServiceImpl appointmentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);  // Initialize mocks
  }

  @Test
  void testCreateAppointment_noConflicts_success() {

    CreateAppointmentDTO appointmentDTO = new CreateAppointmentDTO();
    appointmentDTO.setProviderId(1L);
    appointmentDTO.setUserId(2L);
    appointmentDTO.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDTO.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));
    appointmentDTO.setStatus("scheduled");
    appointmentDTO.setServiceType("consultation");
    appointmentDTO.setComments("Test appointment");

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(0);

    Appointment result = appointmentService.createAppointment(appointmentDTO);

    assertNotNull(result);
    assertEquals(1L, result.getProviderId());
    assertEquals(2L, result.getUserId());
    assertEquals(LocalDateTime.of(2024, 10, 15, 10, 0), result.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 10, 15, 11, 0), result.getEndDateTime());

    verify(appointmentMapper).createAppointment(any(Appointment.class));
  }

  @Test
  void testCreateAppointment_timeConflict_throwsException() {

    CreateAppointmentDTO appointmentDTO = new CreateAppointmentDTO();
    appointmentDTO.setProviderId(1L);
    appointmentDTO.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDTO.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        appointmentService.createAppointment(appointmentDTO));
    assertEquals(
        "The selected time slot is not available or conflicts with an existing appointment.",
        exception.getMessage());
  }

//  public void testGetAppointmentHistory() {
//    // Creating mock data
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
//    List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);
//
//    // Mocking the service call
//    when(appointmentService.getAppointmentHistory(1L, 1L)).thenReturn(mockAppointments);
//
//    // Calling the controller method
//    ResponseEntity<List<Map<String, Object>>> response = appointmentController.getAppointmentHistory(1L, 1L);
//
//    // Validating the response
//    assertEquals(200, response.getStatusCodeValue());
//    assertEquals(2, response.getBody().size());
//    assertEquals("completed", response.getBody().get(0).get("Status"));
//    assertEquals("canceled", response.getBody().get(1).get("Status"));
//  }

}
